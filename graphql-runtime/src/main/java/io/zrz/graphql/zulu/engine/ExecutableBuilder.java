package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.doc.GQLPreparedOperation;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableInputContext;
import io.zrz.graphql.zulu.executable.ExecutableInputField;
import io.zrz.graphql.zulu.executable.ExecutableInputType;
import io.zrz.graphql.zulu.executable.ExecutableInvoker;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.zulu.types.ZField;

/**
 * state specific to building.
 * 
 * @author theo
 *
 */

class ExecutableBuilder {

  private static Logger log = LoggerFactory.getLogger(ExecutableBuilder.class);

  private ZuluEngine engine;
  private GQLPreparedOperation op;
  private List<ZuluWarning> warnings = null;

  ExecutableBuilder(ZuluEngine engine, GQLPreparedOperation op) {
    this.engine = engine;
    this.op = op;
  }

  /**
   * 
   */

  List<ZuluWarning> warnings() {
    return this.warnings;
  }

  /**
   * build an executable for this operation and return a MethodHandle for invoking it.
   * 
   * the {@link MethodHandle} takes the root context instance, and parameter provider. it returns the result of the
   * query.
   * 
   * the executable will use constant values where possible, and invocations will be folded where they can.
   * 
   */

  ZuluExecutable build() {
    ExecutableOutputType type = engine.schema().rootType(op.type()).get();
    return new ZuluExecutable(this, type);
  }

  /**
   * 
   * @param exec
   * @return
   */

  ImmutableList<ZuluSelection> build(ZuluExecutable exec) {
    return build(exec, op.selection());
  }

  /**
   * 
   * @param exec
   * @param selections
   * @return
   */

  ImmutableList<ZuluSelection> build(ZuluSelectionContainer parent, List<? extends GQLPreparedSelection> selections) {

    return selections
        .stream()
        .sequential()
        .map(sel -> build(parent, sel))
        .filter(sel -> !Objects.isNull(sel))
        .collect(ImmutableList.toImmutableList());

  }

  /**
   * build a single ZuluSelection from a selection on a parent.
   * 
   * @param exec
   * @param type
   * @param sel
   * @return
   */

  private ZuluSelection build(ZuluSelectionContainer parent, GQLPreparedSelection sel) {

    // find this field in the parent container.
    ExecutableOutputField field = parent.outputType()
        .field(sel.fieldName())
        .orElse(null);

    if (field == null) {
      addWarning(new ZuluWarning.MissingField(parent.outputType(), sel));
      return null;
    }

    MethodHandle handle = applyField(sel, field);

    if (handle == null) {
      // field apply will have generated warning/error if invalid.
      return null;
    }

    if (sel.subselections().isEmpty()) {
      // this is a leaf, so no other children.

      switch (field.fieldType().logicalTypeKind()) {
        case ENUM:
        case SCALAR:
          // these are the only types which can be for sure output as leafs.
          // we output this value directly to the caller.
          return new ZuluLeafSelection(parent, field, sel, handle);
        case UNION:
        case INPUT:
        case INTERFACE:
        case OUTPUT:
        default:
          break;
      }

      addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.NONLEAF_SELECTION, field, sel));
      return null;

    }

    // non leaf selection

    switch (field.fieldType().logicalTypeKind()) {
      case ENUM:
      case SCALAR:
        // these are the only types which can be for sure output as leafs.
        // we output this value directly to the caller.
        addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.LEAF_SELECTION, field, sel));
        return null;
      case OUTPUT:
        break;
      case UNION:
      case INPUT:
      case INTERFACE:
      default:
        addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.NOT_SUPPORTED, field, sel));
        return null;
    }

    return new ZuluContainerSelection(parent, field, sel, handle, this);

  }

  /**
   * returns a handle for invoking this field. the handle takes the receiver context & request. it returns the result.
   * 
   * @param sel
   *          The selection to execute.
   * 
   * @param field
   * @return
   */

  private MethodHandle applyField(GQLPreparedSelection sel, ExecutableOutputField field) {

    Objects.requireNonNull(field);

    ExecutableInvoker invoker = field.invoker();

    if (invoker == null) {
      addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.INVALID_HANDLER, field, sel));
      return null;
    }

    ExecutableInputType params = field
        .invoker()
        .parameters()
        .orElse(null);

    MethodHandle handle = invocationHandle(field, field.invoker().methodHandle());

    if (params == null) {
      // no parameters, so just return the method handle directly.
      return checkHandlerSignature(field, handle);
    }

    int offset = field.contextParameters().size() - 2;

    for (ExecutableInputField param : params.fieldValues()) {

      handle = map(param, sel, handle, offset);

      offset++;

      if (handle == null) {
        // parameter mapper will have generated the error code.
        return null;
      }

    }

    return checkHandlerSignature(field, handle);

  }

  private MethodHandle checkHandlerSignature(ExecutableOutputField field, MethodHandle handle) {

    MethodType type = handle.type();

    Class<?> returnType = type.returnType();
    List<Class<?>> params = type.parameterList();

    if (field.fieldType().arity() > 0) {

      if (!returnType.isArray()) {
        throw new IllegalArgumentException("expected array return, got " + returnType);
      }

    }

    if (params.size() != 2) {
      throw new IllegalArgumentException("failed to bind all parameters: " + params);
    }

    if (returnType.equals(Void.TYPE)) {
      throw new IllegalArgumentException("can't return void from handler");
    }

    return handle;
  }

  /**
   * wraps an invocation handle before passing into the pipeline.
   * 
   * @param field
   * 
   * @param methodHandle
   * @return
   */

  public MethodHandle invocationHandle(ExecutableOutputField field, MethodHandle mh) {

    mh = MethodHandles.dropArguments(mh, 0, ZuluResultReceiver.class);

    for (ExecutableInputContext ctx : field.contextParameters()) {

      mh = insertContextParam(mh, ctx.index() + 2, ctx);

    }

    return mh;
  }

  private MethodHandle insertContextParam(MethodHandle mh, int index, ExecutableInputContext ctx) {

    TypeToken<?> targetType = ctx.javaType();

    if (targetType.getRawType().equals(ExecutableSchema.class)) {

      return MethodHandles.insertArguments(mh, index, new Object[] { this.engine.schema() });

    }

    throw new IllegalArgumentException("unable to calculate value for @GQLContext " + targetType);

  }

  /**
   * inserts an argument that provides the required value.
   * 
   * @param param
   *          the input parameter definition.
   * @param sel
   *          the selection on the field.
   * 
   */

  private MethodHandle map(ExecutableInputField param, GQLPreparedSelection sel, MethodHandle target, int offset) {

    if (target == null) {
      throw new IllegalArgumentException("target");
    }

    ZField provided = sel.parameters()
        .flatMap(selparams -> selparams.field(param.fieldName()))
        .orElse(null);

    if (provided == null) {
      addWarning(new ZuluWarning.MissingRequiredParameter(param, sel));
      return null;
    }

    //
    if (!engine.compatible(provided.fieldType(), param.fieldType())) {
      addWarning(new ZuluWarning.IncompatibleTypes(param, provided, sel));
      return null;
    }

    // if the value is constant, we can bind now and avoid doing anything at invocation time.
    if (provided.constantValue().isPresent()) {
      return MethodHandles.insertArguments(target, param.index() - offset, engine.get(param, provided.constantValue().get()));
    }

    // the value is a variable (or depends on the output of another field/operation), so we need to defer to runtime.
    // however we do know the type, so let's check to make sure it's valid.

    addWarning(new ZuluWarning.MissingRequiredParameter(param, sel));

    return null;

  }

  /**
   * adds a warning generated during compilation.
   * 
   * @param warning
   *          the warning to add.
   */

  private void addWarning(ZuluWarning warning) {
    if (this.warnings == null) {
      this.warnings = new ArrayList<>();
    }
    this.warnings.add(warning);
  }

}
