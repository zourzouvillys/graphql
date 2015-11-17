package io.joss.graphql.core.lang;

public class DuplicateTypeNameException extends RuntimeException
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public DuplicateTypeNameException(final String name)
  {
    super(String.format("Duplicate type name '%s'", name));
  }

}
