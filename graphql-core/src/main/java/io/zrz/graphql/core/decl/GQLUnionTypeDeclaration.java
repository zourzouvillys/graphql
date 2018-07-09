package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLUnionTypeDeclaration implements GQLExtendableTypeDeclaration {

  @Override
  public GQLTypeDeclKind typeKind() {
    return GQLTypeDeclKind.UNION;
  }

  public abstract List<GQLDeclarationRef> types();

  @Override
  public final <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitUnion(this);
  }

  public static ImmutableGQLUnionTypeDeclaration.Builder builder() {
    return ImmutableGQLUnionTypeDeclaration.builder();
  }

}
