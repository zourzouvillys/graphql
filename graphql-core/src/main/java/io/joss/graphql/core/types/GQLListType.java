package io.joss.graphql.core.types;

import io.joss.graphql.core.lang.GQLTypeVisitor;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * A type modifier which indicates the contained type is a list of the specified type.
 * 
 * @author theo
 *
 */

@Wither
@Builder
public final class GQLListType implements GQLTypeReference
{

  private final GQLTypeReference wrappedType;

  public GQLTypeReference type()
  {
    return this.wrappedType;
  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor)
  {
    return visitor.visitList(this);
  }

  @Override
  public String toString()
  {
    return String.format("[%s]", type().toString());
  }

}
