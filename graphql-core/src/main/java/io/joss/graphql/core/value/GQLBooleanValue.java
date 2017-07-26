package io.joss.graphql.core.value;

public enum GQLBooleanValue implements GQLScalarValue {

  TRUE {

    @Override
    public boolean value() {
      return true;
    }

  },
  FALSE {

    @Override
    public boolean value() {
      return false;
    }

  };

  public static GQLBooleanValue ofValue(final boolean value) {
    return value ? TRUE : FALSE;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitBooleanValue(this);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.Boolean;
  }

  public abstract boolean value();

}
