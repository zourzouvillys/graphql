package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLTypeDeclKind;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLEnumDeclaration implements GQLExtendableTypeDeclaration {

  public abstract List<GQLEnumValue> values();

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitEnum(this);
  }

  public static ImmutableGQLEnumDeclaration.Builder builder() {
    return ImmutableGQLEnumDeclaration.builder();
  }

  @Override
  public GQLTypeDeclKind typeKind() {
    return GQLTypeDeclKind.ENUM;
  }

}
