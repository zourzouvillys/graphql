package io.joss.graphql.core.decl;

import java.util.Map;

import io.joss.graphql.core.types.GQLDeclarationRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
@Builder(builderClassName = "Builder")
public final class GQLSchemaDeclaration implements GQLDeclaration {

  private final String name;
  private final String description;

  @Singular
  private final Map<String, GQLDeclarationRef> entries;

  public static class Builder {
  }

  @Override
  public String description() {
    return this.description;
  }

  public Map<String, GQLDeclarationRef> entries() {
    return this.entries;
  }

  @Override
  public <R> R apply(GQLDeclarationVisitor<R> visitor) {
    return visitor.visitSchemaDeclaration(this);
  }

}
