package io.joss.graphql.core.binder;

import io.joss.graphql.core.binder.model.OutputClassBinding;
import io.joss.graphql.core.binder.model.OutputClassField;

public class ObjectFieldException extends RuntimeException
{

  private OutputClassField field;
  private OutputClassBinding binding;

  public ObjectFieldException(OutputClassBinding binding, OutputClassField field, Exception ex)
  {
    super(ex);
    this.binding = binding;
    this.field = field;
  }

  @Override
  public String getMessage()
  {
    return String.format("Error processing fields of %s.%s: %s", binding.name(), field.name(), super.getCause().getMessage());
  }

}
