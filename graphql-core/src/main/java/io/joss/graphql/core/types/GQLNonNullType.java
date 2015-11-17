package io.joss.graphql.core.types;

import io.joss.graphql.core.lang.GQLTypeVisitor;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@Wither
@Builder
public final class GQLNonNullType implements GQLTypeReference
{

  private final GQLTypeReference wrappedType;

  private GQLNonNullType(GQLTypeReference wrappedType)
  {
    if (wrappedType instanceof GQLNonNullType)
    {
      throw new IllegalArgumentException("Can't have a non null type ref a non null ttpe");
    }
    this.wrappedType = wrappedType;
  }

  public GQLTypeReference type()
  {
    return this.wrappedType;
  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor)
  {
    return visitor.visitNonNull(this);
  }

}
