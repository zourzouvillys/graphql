package io.zrz.graphql.core.decl;

/**
 * A declaration of a type, e.g object, type, interface, scalar, etc.
 *
 * the "schema" element is not a type.
 *
 * @author theo
 *
 */

public interface GQLTypeDeclaration extends GQLDeclaration {

  String name();

  <R> R apply(GQLTypeDeclarationVisitor<R> visitor);

  @Override
  default <R> R apply(GQLDeclarationVisitor<R> visitor) {
    return visitor.visitTypeDeclaration(this);
  }

}
