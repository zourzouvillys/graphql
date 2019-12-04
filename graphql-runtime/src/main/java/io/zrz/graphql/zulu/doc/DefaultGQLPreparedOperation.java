package io.zrz.graphql.zulu.doc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.types.GQLTypeRefKind;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZPrimitiveScalarType;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZStructValue;
import io.zrz.zulu.values.ZValue;

/**
 * a prepared GQL operation, which has performed rudimentary variable resolution and binding.
 */

public class DefaultGQLPreparedOperation implements GQLPreparedOperation {

  private final GQLSelectedOperation op;
  private final GQLTypeResolver resolver;
  private final DefaultGQLPreparedDocument doc;
  private final OpInputType inputType;
  private final List<DefaultGQLPreparedSelection> selection;

  public DefaultGQLPreparedOperation(final DefaultGQLPreparedDocument doc, final GQLTypeResolver resolver, final GQLSelectedOperation op) {
    this.doc = doc;
    this.resolver = resolver;
    this.op = op;
    this.inputType = new OpInputType();
    this.selection =
      this.op
        .operation()
        .selections()
        .stream()
        .flatMap(x -> x.apply(new SelectionGenerator(this)))
        .collect(Collectors.toList());
  }

  /**
   * the name of this operation, if defined.
   */

  @Override
  public Optional<String> operationName() {
    return Optional.ofNullable(this.op.operationName());
  }

  GQLOperationDefinition operation() {
    return this.op.operation();
  }

  /**
   * the type of operation, e.g query, mutation, or subscription.
   */

  @Override
  public GQLOpType type() {
    return this.op.operation().type();
  }

  /**
   * directives applied on the operation itself.
   */

  @Override
  public List<ZAnnotation> annotations() {
    return this.op.operation().directives().stream().map(x -> new ZAnnotation() {

      @Override
      public Optional<ZStructValue> value() {
        return null;
      }

      @Override
      public String name() {
        return x.name();
      }

    })
      .collect(Collectors.toList());
  }

  public class OpInputField implements ZField {

    private final GQLVariableDefinition var;
    private final Optional<ZValue> defaultValue;

    public OpInputField(final GQLVariableDefinition var) {
      this.var = var;
      final GQLValue defaultValue = var.defaultValue();
      if (defaultValue == null)
        this.defaultValue = Optional.empty();
      else
        this.defaultValue =
          defaultValue
            .apply(new ConstantZValueValueExtractor(DefaultGQLPreparedOperation.this, null, null));
    }

    @Override
    public ZTypeUse fieldType() {
      return ZTypeUse.of(ZPrimitiveScalarType.STRING);
    }

    @Override
    public Optional<ZValue> defaultValue() {
      return this.defaultValue;
    }

    @Override
    public List<ZAnnotation> annotations() {
      // TODO Auto-generated method stub
      return Collections.emptyList();
    }

    @Override
    public String fieldName() {
      return this.var.name();
    }

    @Override
    public boolean isOptional() {
      return this.var.type().typeRefKind() != GQLTypeRefKind.NOT_NULL;
    }

  }

  public class OpInputType implements ZStructType {

    private final Map<String, OpInputField> fields;

    public OpInputType() {
      this.fields =
        DefaultGQLPreparedOperation.this.op.operation()
          .vars()
          .stream()
          .collect(Collectors.toMap(x -> x.name(), x -> new OpInputField(x)));
    }

    @Override
    public Map<String, OpInputField> fields() {
      return this.fields;
    }

    public void validate(final GQLPreparedValidationListener listener) {
      // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
      return this.fields.entrySet()
        .stream()
        .map(e -> String.format("$%s: %s", e.getKey(), e.getValue().fieldType()))
        .collect(Collectors.joining(", "));
    }

  }

  /**
   * the expected input variables, as an anonymous {@link ZStructType} instance.
   */

  @Override
  public OpInputType inputType() {
    return this.inputType;
  }

  /**
   * each of the selections.
   */

  @Override
  public List<DefaultGQLPreparedSelection> selection() {
    return this.selection;
  }

  GQLFragmentDefinition fragment(final String name) {
    return this.op.doc().fragment(name);
  }

  GQLTypeResolver typeResolver() {
    return this.resolver;
  }

  @Override
  public GQLPreparedDocument document() {
    return this.doc;
  }

  @Override
  public void validate(final GQLPreparedValidationListener listener) {
    if (this.inputType != null) {
      this.inputType.validate(listener);
    }
    this.selection.forEach(sel -> sel.validate(listener));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.operation().type());
    sb.append(" ");
    sb.append(this.operationName().orElse("<unnamed>"));
    sb.append("(");
    sb.append(this.inputType);
    sb.append(")");
    sb.append(" {\n");
    this.selection.forEach(sel -> sb.append("  ").append(sel).append("\n"));
    sb.append("}\n");
    return sb.toString();
  }

}
