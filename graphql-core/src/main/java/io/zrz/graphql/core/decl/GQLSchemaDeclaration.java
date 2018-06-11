package io.zrz.graphql.core.decl;

import java.util.Map;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;

@Value.Immutable(copy = true)
public abstract class GQLSchemaDeclaration implements GQLDeclaration {

  public abstract String name();

  public abstract Map<String, GQLDeclarationRef> entries();

  @Override
  public <R> R apply(GQLDeclarationVisitor<R> visitor) {
    return visitor.visitSchemaDeclaration(this);
  }

}
