package io.zrz.graphql.core.lang;

public class UnresolvableTypeNameException extends RuntimeException
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public UnresolvableTypeNameException(final String name)
  {
    super(String.format("Invalid type name '%s'", name));
  }

}
