package io.zrz.graphql.core.types;

import java.util.Collection;
import java.util.Collections;

import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.lang.GQLTypeVisitor;

@Value.Immutable(copy = true)
public abstract class GQLNonNullType implements GQLTypeReference {

  public abstract GQLTypeReference type();

  @Value.Default
  public Collection<GQLDirective> directives() {
    return Collections.emptyList();
  }

  public abstract GQLNonNullType withType(GQLTypeReference value);

  public abstract GQLNonNullType withDirectives(Collection<GQLDirective> value);

  public static GQLNonNullType of(GQLTypeReference wrappedType, Collection<GQLDirective> directives) {

    if (wrappedType instanceof GQLNonNullType) {
      throw new IllegalArgumentException("Can't have a non null type ref a non null ttpe");
    }

    return ImmutableGQLNonNullType.builder().type(wrappedType).directives(directives).build();

  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor) {
    return visitor.visitNonNull(this);
  }

}
