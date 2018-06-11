package io.zrz.graphql.core.decl;

import org.immutables.value.Value;

public interface GQLExtendableTypeDeclaration extends GQLTypeDeclaration {

  @Value.Default
  default boolean isExtension() {
    return false;
  }

  GQLExtendableTypeDeclaration withIsExtension(boolean value);

}
