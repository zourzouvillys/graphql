package io.zrz.graphql.core.types;

import java.util.List;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.Wither;

/**
 * A type modifier which indicates the contained type is a list of the specified
 * type.
 *
 * @author theo
 *
 */

@Wither
@Builder
public final class GQLListType implements GQLTypeReference {

  private final GQLTypeReference wrappedType;
  @Singular
  private final List<GQLDirective> directives;

  public GQLTypeReference type() {
    return this.wrappedType;
  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor) {
    return visitor.visitList(this);
  }

  @Override
  public String toString() {
    return String.format("[%s]", this.type().toString());
  }

}
