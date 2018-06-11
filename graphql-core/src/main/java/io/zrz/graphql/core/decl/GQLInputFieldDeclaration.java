package io.zrz.graphql.core.decl;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.value.GQLValue;

@Value.Immutable(copy = true)
public abstract class GQLInputFieldDeclaration implements GQLFieldDeclaration {

  @Nullable
  public abstract GQLValue defaultValue();

  @Override
  public abstract GQLInputFieldDeclaration withName(String ref);

  @Override
  public abstract GQLInputFieldDeclaration withDescription(String ref);

  @Override
  public abstract GQLInputFieldDeclaration withType(GQLTypeReference ref);

  @Override
  public abstract GQLInputFieldDeclaration withDeprecationReason(String ref);

  @Override
  public abstract GQLInputFieldDeclaration withDirectives(GQLDirective... ref);

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("GQLInputField(");
    sb.append(this.name()).append(": ").append(this.type());
    sb.append(')');
    if (this.defaultValue() != null) {
      sb.append(" = ");
      sb.append(this.defaultValue());
    }
    return sb.toString();

  }

  public static ImmutableGQLInputFieldDeclaration.Builder builder() {
    return ImmutableGQLInputFieldDeclaration.builder();
  }

}
