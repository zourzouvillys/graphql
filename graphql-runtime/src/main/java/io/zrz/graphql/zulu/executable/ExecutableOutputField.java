package io.zrz.graphql.zulu.executable;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.ZOutputField;
import io.zrz.graphql.zulu.annotations.GQLContext;
import io.zrz.graphql.zulu.binding.JavaBindingUtils;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;

public final class ExecutableOutputField implements ZOutputField, ExecutableElement {

  private final ExecutableOutputType receiverType;
  private final JavaOutputField field;
  private final ExecutableInputType params;
  private ReturnTypeUse returnType;

  /**
   * context parameters needed for this field.
   */

  private ImmutableList<ExecutableInputContext> context;

  ExecutableOutputField(ExecutableOutputType receiverType, Symbol symbol, JavaOutputField field, BuildContext types) {

    this.receiverType = receiverType;
    this.field = field;

    this.params = new ExecutableInputType(
        this,
        field.inputFields()
            .filter(f -> !f.annotation(GQLContext.class).isPresent())
            .map(f -> new ExecutableInputField(this, f, types))
            .collect(ImmutableList.toImmutableList()));

    this.context = field.inputFields()
        .filter(f -> f.annotation(GQLContext.class).isPresent())
        .map(f -> new ExecutableInputContext(this, f, types))
        .collect(ImmutableList.toImmutableList());

    this.returnType = new ReturnTypeUse(this, types, field.returnType());

  }

  JavaOutputField field() {
    return this.field;
  }

  /**
   * contextual parameters needed for this field.
   */

  public ImmutableList<ExecutableInputContext> contextParameters() {
    return this.context;
  }

  @Override
  public ExecutableTypeUse fieldType() {
    return this.returnType.use();
  }

  /**
   * the type that this field is part of.
   */

  public ExecutableOutputType receiverType() {
    return this.receiverType;
  }

  /**
   * the name of this field.
   */

  public String fieldName() {
    return field.fieldName();
  }

  /**
   * each of the parameters needed for this field.
   */

  @Override
  public Optional<ExecutableInputType> parameters() {
    if (params.fields().isEmpty())
      return Optional.empty();
    return Optional.of(params);
  }

  /**
   * each of the parameters needed for this field.
   */

  @Override
  public ExecutableInputField parameter(String pname) {
    if (params.fields().isEmpty())
      return null;
    return params.field(pname).get();
  }

  /**
   * a slow-path invocation using the reflection API. prefer the invoker.
   * 
   * @param viewer
   *          The viewer request.
   * @param context
   *          The context this field is being executed in (e.g, an instance of the receiver)
   * @param args
   *          The (java) parameters for executing this field. must match the type and order of the
   *          {@link #inputFields()}.
   * 
   * @return The return value, or an exception if there was an error.
   */

  public <T, V, C> T invoke(V request, C context, Object... args) {
    return this.field.invoke(request, context, args);
  }

  public ExecutableInvoker invoker() {
    return new ExecutableInvoker(this, this.field.invoker(), this.returnType);
  }

  @Override
  public String documentation() {
    return field.documentation();
  }

  @Override
  public String toString() {
    return "field " + this.receiverType.typeName() + "." + this.fieldName()
        + " (defined by " + JavaBindingUtils.toString(this.field.origin().get()) + ")";
  }

  public Optional<? extends AnnotatedElement> origin() {
    return field.origin();
  }

}
