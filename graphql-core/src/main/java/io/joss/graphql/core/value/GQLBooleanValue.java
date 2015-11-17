package io.joss.graphql.core.value;

public enum GQLBooleanValue implements GQLValue
{

  TRUE, FALSE;

  public static GQLBooleanValue ofValue(final boolean value)
  {
    return value ? TRUE : FALSE;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor)
  {
    return visitor.visitBooleanValue(this);
  }

}
