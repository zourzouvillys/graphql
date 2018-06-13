package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.ZOutputField;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;

public final class ExecutableOutputField implements ZOutputField, ExecutableElement {

  private final ExecutableOutputType receiverType;
  private final JavaOutputField field;
  private final ExecutableInputType params;
  private ExecutableTypeUse fieldType;

  ExecutableOutputField(ExecutableOutputType receiverType, Symbol symbol, JavaOutputField field, BuildContext types) {
    this.receiverType = receiverType;
    this.field = field;
    this.params = new ExecutableInputType(
        this,
        field.inputFields()
            .map(f -> new ExecutableInputField(this, f, types))
            .collect(ImmutableList.toImmutableList()));
    this.fieldType = types.use(this, field.returnType());
  }

  @Override
  public ExecutableTypeUse fieldType() {
    return this.fieldType;
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
   * the method handle for executing this field.
   */

  public MethodHandle methodHandle() {
    throw new RuntimeException("not implemented");
  }

  /**
   * a slow-path invocation using the reflection API. prefer the methodHandle.
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

  public List<String> documentation() {
    return this.field.documentation();
  }

}
