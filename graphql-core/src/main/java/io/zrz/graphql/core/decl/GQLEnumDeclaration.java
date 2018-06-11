package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
public abstract class GQLEnumDeclaration implements GQLExtendableTypeDeclaration {

  public abstract List<GQLEnumValue> values();

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitEnum(this);
  }

  public static ImmutableGQLEnumDeclaration.Builder builder() {
    return ImmutableGQLEnumDeclaration.builder();
  }

}
