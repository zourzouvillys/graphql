package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.doc.DefaultGQLPreparedOperation.OpInputType;
import io.zrz.graphql.zulu.doc.GQLPreparedOperation;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.doc.GQLSelectionTypeCriteria;
import io.zrz.graphql.zulu.doc.RuntimeParameterHolder;
import io.zrz.graphql.zulu.executable.ExecutableInputContext;
import io.zrz.graphql.zulu.executable.ExecutableInputField;
import io.zrz.graphql.zulu.executable.ExecutableInputType;
import io.zrz.graphql.zulu.executable.ExecutableInvoker;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableReceiverType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZTypeUse;

/**
 * state specific to building.
 *
 * @author theo
 *
 */

class ExecutableBuilder {

  private static final int CONTEXT_ARGS = 2;

  private static Logger log = LoggerFactory.getLogger(ExecutableBuilder.class);

  private final ZuluEngine engine;
  private final GQLPreparedOperation op;
  private List<ZuluWarning> warnings = null;

  ExecutableBuilder(final ZuluEngine engine, final GQLPreparedOperation op) {
    this.engine = engine;
    this.op = op;
  }

  Optional<String> operationName() {
    return this.op.operationName();
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
    final ExecutableOutputType type = this.engine.schema().rootType(this.op.type()).get();
    return new ZuluExecutable(this, type);
  }

  /**
   *
   * @param exec
   * @return
   */

  ImmutableList<ZuluSelection> build(final ZuluExecutable exec) {
    return this.build(exec, this.op.selection());
  }

  /**
   *
   * @param exec
   * @param selections
   * @return
   */

  ImmutableList<ZuluSelection> build(final ZuluSelectionContainer parent, final List<? extends GQLPreparedSelection> selections) {

    return selections
        .stream()
        .sequential()
        .map(sel -> this.build(parent, sel))
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

  private ZuluSelection build(final ZuluSelectionContainer parent, final GQLPreparedSelection sel) {

    // get the receiver that the field will be selected from.
    final ExecutableReceiverType receiverType = this.calculateReceiver(parent, sel);

    if (receiverType == null) {
      // calculareReceiver will have warned
      return null;
    }

    // find this field in the parent container.
    final ExecutableOutputField field = receiverType
        .field(sel.fieldName())
        .orElse(null);

    if (field == null) {
      this.addWarning(new ZuluWarning.MissingField(receiverType, sel));
      return null;
    }

    final TypeTokenMethodHandle handle = this.applyField(sel, parent.outputType(), receiverType, field);

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
          return new ZuluLeafSelection(parent, field, sel, handle, receiverType);
        case UNION:
        case INPUT:
        case INTERFACE:
        case OUTPUT:
        default:
          break;
      }

      this.addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.NONLEAF_SELECTION, field, sel));
      return null;

    }

    // non leaf selection

    switch (field.fieldType().logicalTypeKind()) {
      case ENUM:
      case SCALAR:
        // these are the only types which can be for sure output as leafs.
        // we output this value directly to the caller.
        this.addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.LEAF_SELECTION, field, sel));
        return null;
      case OUTPUT:
      case INTERFACE:
        break;
      case UNION:
      case INPUT:
      default:
        this.addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.NOT_SUPPORTED, field, sel));
        return null;
    }

    return new ZuluContainerSelection(parent, field, sel, handle, receiverType, this);

  }

  /**
   * calculate the type we will get the field from.
   *
   * @param parent
   * @param sel
   * @return
   */

  private ExecutableReceiverType calculateReceiver(final ZuluSelectionContainer parent, final GQLPreparedSelection sel) {

    if (sel.typeCritera().isPresent()) {

      // it's a fragment spread
      final GQLSelectionTypeCriteria typeCriteria = sel.typeCritera().get();
      final String receiverTypeName = typeCriteria.refType().name();
      final ExecutableType receiverType = this.engine.schema().resolveType(receiverTypeName);

      if (receiverType == null) {
        this.addWarning(new ZuluWarning.UnknownTypeSymbol(parent.outputType(), receiverTypeName, sel));
        return null;
      }

      switch (receiverType.logicalKind()) {
        case INTERFACE:
        case OUTPUT:
          break;
        default:
          this.addWarning(new ZuluWarning.OutputTypeWarning(ZuluWarningKind.INVALID_SPREAD, parent.outputType(), sel));
          return null;
      }

      return (ExecutableReceiverType) receiverType;

    }

    return parent.outputType();

  }

  /**
   * returns a handle for invoking this field. the handle takes the receiver context & request. it returns the result.
   *
   * @param sel
   *                       The selection to execute.
   * @param receiverType
   *                       the receiver type of the selection - may be different than the field type (due to spread).
   * @param field
   * @return
   */

  private TypeTokenMethodHandle applyField(
      final GQLPreparedSelection sel,
      final ExecutableReceiverType contextType,
      final ExecutableReceiverType receiverType,
      final ExecutableOutputField field) {

    Objects.requireNonNull(field);

    final ExecutableInvoker invoker = field.invoker();

    if (invoker == null) {
      this.addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.INVALID_HANDLER, field, sel));
      return null;
    }

    final ExecutableInputType params = field
        .invoker()
        .parameters()
        .orElse(null);

    TypeTokenMethodHandle handle = this.invocationHandle(field, field.invoker().methodHandle());

    if (receiverType != contextType) {
      // cast to the receiver type, else return null
      handle = handle.guardReceiverType(contextType.javaType(), receiverType);
    }

    if (params == null) {

      // no parameters, so just return the method handle directly.

      if (sel.parameters().isPresent()) {

        // providing parameters when we don't need any?

        final List<String> extras = sel.parameters().get()
            .fieldNames()
            .collect(Collectors.toList());

        if (!extras.isEmpty()) {

          this.addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.UNKNOWN_PARAMETER, field, sel,
              "unknown parameter"
                  + (extras.size() == 1 ? "" : "s")
                  + " "
                  + (extras.size() == 1 ? "'" + extras.get(0) + "'" : extras.toString())
                  + " for selection on field '"
                  + sel.fieldName() + "' in type '"
                  + receiverType.typeName()
                  + "'"));

          return null;

        }

      }

      return this.checkHandlerSignature(field, handle);
    }

    //
    if (sel.parameters().isPresent()) {

      final List<String> extras = sel.parameters()
          .get()
          .fieldNames()
          .filter(a -> !params.contains(a))
          .collect(Collectors.toList());

      if (!extras.isEmpty()) {

        this.addWarning(new ZuluWarning.OutputFieldWarning(ZuluWarningKind.UNKNOWN_PARAMETER, field, sel,
            "unknown parameter"
                + (extras.size() == 1 ? "" : "s")
                + " "
                + (extras.size() == 1 ? "'" + extras.get(0) + "'" : extras.toString())
                + " for selection on field '"
                + sel.fieldName() + "' in type '"
                + receiverType.typeName()
                + "'"));

        return null;

      }

    }
    //

    int offset = field.contextParameters().size() - CONTEXT_ARGS;

    for (final ExecutableInputField param : params.fieldValues()) {

      handle = this.map(param, sel, handle, offset);

      offset++;

      if (handle == null) {
        // parameter mapper will have generated the error/warning note.
        return null;
      }

    }

    return this.checkHandlerSignature(field, handle);

  }

  private TypeTokenMethodHandle checkHandlerSignature(final ExecutableOutputField field, final TypeTokenMethodHandle handle) {

    final MethodType type = handle.type();

    final Class<?> returnType = type.returnType();
    final List<Class<?>> params = type.parameterList();

    if (field.fieldType().arity() > 0) {

      if (!returnType.isArray()) {
        throw new IllegalArgumentException("expected array return, got " + returnType);
      }

    }

    if (params.size() != CONTEXT_ARGS) {
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

  public TypeTokenMethodHandle invocationHandle(final ExecutableOutputField field, MethodHandle mh) {

    mh = MethodHandles.dropArguments(mh, 0, ZuluRequestContext.class);

    for (final ExecutableInputContext ctx : field.contextParameters()) {

      mh = this.insertContextParam(mh, ctx.index() + CONTEXT_ARGS, ctx);

    }

    return new TypeTokenMethodHandle(mh);
  }

  private MethodHandle insertContextParam(final MethodHandle mh, final int index, final ExecutableInputContext ctx) {

    final TypeToken<?> targetType = ctx.javaType();

    if (targetType.getRawType().equals(ExecutableSchema.class)) {

      return MethodHandles.insertArguments(mh, index, new Object[] { this.engine.schema() });

    }

    throw new IllegalArgumentException("unable to calculate value for @GQLContext " + targetType);

  }

  /**
   * inserts an argument that provides the required value.
   *
   * @param param
   *                the input parameter definition.
   * @param sel
   *                the selection on the field.
   *
   */

  private TypeTokenMethodHandle map(final ExecutableInputField param, final GQLPreparedSelection sel, final TypeTokenMethodHandle target, final int offset) {

    if (target == null) {
      throw new IllegalArgumentException("target");
    }

    final ZField provided = sel
        .parameters()
        .flatMap(selparams -> selparams.field(param.fieldName()))
        .orElse(null);

    if (provided == null) {

      if (param.isNullable()) {

        // hack for Optional
        if (param.javaType().getRawType().equals(Optional.class)) {
          return target.insertArguments(param.index() - offset, new Object[] { Optional.empty() });
        }

        return target.insertArguments(param.index() - offset, new Object[] { null });
      }

      this.addWarning(new ZuluWarning.MissingRequiredParameter(param, sel));
      return null;
    }

    final ExecutableTypeUse fieldType = param.fieldType();

    //
    if (!this.engine.compatible(provided.fieldType(), fieldType)) {
      this.addWarning(new ZuluWarning.IncompatibleTypes(param, provided, sel));
      return null;
    }

    // if the value is constant, we can bind now and avoid doing anything at invocation time.
    if (provided.constantValue().isPresent()) {
      return target.insertArguments(param.index() - offset, this.engine.get(param, provided.constantValue().get()));
    }

    // the value is a variable (or depends on the output of another field/operation), so we need to defer to runtime.
    // however we do know the type, so let's check to make sure it's valid.

    final ExecutableTypeUse targetType = param.fieldType();

    // the actual type we need to provide, before logical unwrapping (e.g, Optional, List, etc).
    final TypeToken<?> javaType = param.javaType();

    final ZTypeUse providedType = provided.fieldType();

    final RuntimeParameterHolder holder = (RuntimeParameterHolder) provided;

    MethodHandle mh;
    try {

      mh = MethodHandles
          .lookup()
          .findStatic(
              ExecutableBuilder.class,
              "resolveParameter",
              MethodType.methodType(Object.class, ZuluRequestContext.class, ExecutableInputField.class, String.class));

      mh = MethodHandles.insertArguments(mh, 1, param, holder.parameterName());

      mh = mh.asType(mh.type().changeReturnType(javaType.getRawType()));

    }
    catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    MethodHandle mapped = MethodHandles.collectArguments(target.handle(), param.index() - offset, mh);

    // map the arguments.

    final int mapping[] = new int[mapped.type().parameterCount()];
    mapping[0] = 0;
    mapping[1] = 1;
    mapping[2] = 0;

    for (int i = 3; i < mapped.type().parameterCount(); ++i) {
      mapping[i] = i - 1;
    }

    mapped = MethodHandles.permuteArguments(
        mapped,
        mapped.type().dropParameterTypes(2, 3), // new handle type with 1 less param
        mapping);

    return new TypeTokenMethodHandle(mapped);

  }

  public static Object resolveParameter(final ZuluRequestContext ctx, final ExecutableInputField targetType, final String name) {
    return ctx.parameter(name, targetType);
  }

  /**
   * adds a warning generated during compilation.
   *
   * @param warning
   *                  the warning to add.
   */

  private void addWarning(final ZuluWarning warning) {
    if (this.warnings == null) {
      this.warnings = new ArrayList<>();
    }
    this.warnings.add(warning);
  }

  public GQLOperationType operationType() {
    return this.op.type();
  }

  public OpInputType inputType() {
    return this.op.inputType();
  }

}
