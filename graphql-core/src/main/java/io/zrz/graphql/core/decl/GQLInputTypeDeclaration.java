package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable(copy = true, builder = true)
public abstract class GQLInputTypeDeclaration implements GQLExtendableTypeDeclaration {

  public abstract List<GQLInputFieldDeclaration> fields();

  @Override
  public <R> R apply(GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitInput(this);
  }

  public static ImmutableGQLInputTypeDeclaration.Builder builder() {
    return ImmutableGQLInputTypeDeclaration.builder();
  }

}
