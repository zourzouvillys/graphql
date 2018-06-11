package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;

@Value.Immutable(copy = true)
public abstract class GQLUnionTypeDeclaration implements GQLExtendableTypeDeclaration {

  public abstract List<GQLDeclarationRef> types();

  @Override
  public final <R> R apply(GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitUnion(this);
  }

  public static ImmutableGQLUnionTypeDeclaration.Builder builder() {
    return ImmutableGQLUnionTypeDeclaration.builder();
  }

}
