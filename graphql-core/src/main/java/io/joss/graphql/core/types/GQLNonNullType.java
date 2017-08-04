package io.joss.graphql.core.types;

import java.util.Collection;

import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@Wither
@Builder
public final class GQLNonNullType implements GQLTypeReference {

  private final GQLTypeReference wrappedType;
  private final Collection<GQLDirective> directives;

  private GQLNonNullType(GQLTypeReference wrappedType, Collection<GQLDirective> directives) {
    if (wrappedType instanceof GQLNonNullType) {
      throw new IllegalArgumentException("Can't have a non null type ref a non null ttpe");
    }
    this.wrappedType = wrappedType;
    this.directives = directives;
  }

  public GQLTypeReference type() {
    return this.wrappedType;
  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor) {
    return visitor.visitNonNull(this);
  }

}
