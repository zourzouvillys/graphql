package io.zrz.graphql.core.decl;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLTypeDeclKind;

/**
 * As expected by the name, a scalar represents a primitive value in GraphQL. GraphQL responses take the form of a
 * hierarchical tree; the leaves on these trees are GraphQL scalars.
 *
 * All GraphQL scalars are representable as strings, though depending on the response format being used, there may be a
 * more appropriate primitive for the given scalar type, and server should use those types when appropriate.
 *
 * GraphQL provides a number of built‐in scalars, but type systems can add additional scalars with semantic meaning. For
 * example, a GraphQL system could define a scalar called Time which, while serialized as a string, promises to conform
 * to ISO‐8601. When querying a field of type Time, you can then rely on the ability to parse the result with an
 * ISO‐8601 parser and use a client‐specific primitive for time. Another example of a potentially useful custom scalar
 * is Url, which serializes as a string, but is guaranteed by the server to be a valid URL.
 *
 * @author theo
 *
 */

@Value.Immutable(copy = true)
public abstract class GQLScalarTypeDeclaration implements GQLTypeDeclaration {

  @Override
  public GQLTypeDeclKind typeKind() {
    return GQLTypeDeclKind.SCALAR;
  }

  /**
   * The GraphQL language defines a few types as primitive, and as always available to the type system (builtins).
   * Returns true if this type is one of those.
   *
   * @return
   */

  public boolean isPrimitive() {
    switch (this.name()) {
      case "String":
      case "ID":
      case "Float":
      case "Int":
      case "Boolean":
        return true;
    }
    return false;
  }

  @Override
  public final <R> R apply(GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitScalar(this);
  }

  public static ImmutableGQLScalarTypeDeclaration.Builder builder() {
    return ImmutableGQLScalarTypeDeclaration.builder();
  }

}
