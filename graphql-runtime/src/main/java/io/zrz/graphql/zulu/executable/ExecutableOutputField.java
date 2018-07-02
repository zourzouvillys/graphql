package io.zrz.graphql.zulu.executable;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.ZOutputField;
import io.zrz.graphql.zulu.annotations.GQLContext;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;
import io.zrz.graphql.zulu.binding.JavaBindingUtils;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;

public final class ExecutableOutputField implements ZOutputField, ExecutableElement {

  private final ExecutableReceiverType receiverType;
  private final JavaOutputField field;
  private final ExecutableOutputFieldParameters params;
  private final ReturnTypeUse returnType;

  /**
   * context parameters needed for this field.
   */

  private final ImmutableList<ExecutableInputContext> context;

  ExecutableOutputField(final ExecutableReceiverType receiverType, final Symbol symbol, final JavaOutputField field, final BuildContext types) {

    this.receiverType = receiverType;
    this.field = field;

    this.params = new ExecutableOutputFieldParameters(
        this,
        field
            .inputFields()
            .filter(f -> !f.annotation(GQLContext.class).isPresent())
            .map(f -> new ExecutableOutputFieldParam(this, f, types))
            .collect(ImmutableList.toImmutableList()));

    this.context = field
        .inputFields()
        .filter(f -> f.annotation(GQLContext.class).isPresent())
        .map(f -> new ExecutableInputContext(this, f, types))
        .collect(ImmutableList.toImmutableList());

    try {

      final GQLTypeUse[] ants = JavaExecutableUtils
          .getMethodReturnTypeUseAnnotations(field)
          .toArray(GQLTypeUse[]::new);

      // final Type resolvedReturnType = JavaExecutableUtils.getDeclaredMethod((Method)
      // field.origin().get()).getGenericReturnType();

      this.returnType = new ReturnTypeUse(
          this,
          types,
          field.returnType(),
          ants);

    }
    catch (final Throwable ex) {

      throw new RuntimeException("error calculating return type for '" + symbol.typeName + "." + field.fieldName() + "'", ex);

    }

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

  public ExecutableReceiverType receiverType() {
    return this.receiverType;
  }

  /**
   * the name of this field.
   */

  public String fieldName() {
    return this.field.fieldName();
  }

  /**
   * each of the parameters needed for this field.
   */

  @Override
  public Optional<ExecutableOutputFieldParameters> parameters() {
    if (this.params.fields().isEmpty())
      return Optional.empty();
    return Optional.of(this.params);
  }

  /**
   * each of the parameters needed for this field.
   */

  @Override
  public ExecutableOutputFieldParam parameter(final String pname) {
    if (this.params.fields().isEmpty())
      return null;
    return this.params.field(pname).get();
  }

  /**
   * a slow-path invocation using the reflection API. prefer the invoker.
   *
   * @param viewer
   *                  The viewer request.
   * @param context
   *                  The context this field is being executed in (e.g, an instance of the receiver)
   * @param args
   *                  The (java) parameters for executing this field. must match the type and order of the
   *                  {@link #inputFields()}.
   *
   * @return The return value, or an exception if there was an error.
   */

  public <T, V, C> T invoke(final V request, final C context, final Object... args) {
    return this.field.invoke(request, context, args);
  }

  public ExecutableInvoker invoker() {
    return new ExecutableInvoker(this, this.field.invoker(), this.returnType);
  }

  @Override
  public String documentation() {
    return this.field.documentation();
  }

  @Override
  public String toString() {
    return "field " + this.receiverType.typeName() + "." + this.fieldName()
        + " (defined by " + JavaBindingUtils.toString(this.field.origin().get()) + ")";
  }

  public Optional<? extends AnnotatedElement> origin() {
    return this.field.origin();
  }

}
