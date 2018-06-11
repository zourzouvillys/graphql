package io.zrz.graphql.core.types;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.lang.GQLTypeVisitor;

/**
 * A type modifier which indicates the contained type is a list of the specified type.
 *
 * @author theo
 *
 */

@Value.Immutable(copy = true)
public abstract class GQLListType implements GQLTypeReference {

  public abstract GQLTypeReference type();

  public abstract List<GQLDirective> directives();

  public abstract GQLListType withType(GQLTypeReference value);

  public abstract GQLListType withDirectives(GQLDirective... elements);

  public abstract GQLListType withDirectives(Iterable<? extends GQLDirective> elements);

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor) {
    return visitor.visitList(this);
  }

  @Override
  public String toString() {
    return String.format("[%s]", this.type().toString());
  }

}
