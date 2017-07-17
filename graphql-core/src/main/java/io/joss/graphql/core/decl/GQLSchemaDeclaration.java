package io.joss.graphql.core.decl;

import java.util.List;
import java.util.Map;

import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.types.GQLDeclarationRef;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Wither;

@Value
@Wither
final @Builder(builderClassName = "Builder") public class GQLSchemaDeclaration implements GQLDeclaration {

  private final String name;
  private final String description;

  @Singular
  private final Map<String, GQLDeclarationRef> entries;

  @Singular
  private List<GQLDirective> directives;

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

  public List<GQLDirective> directives() {
    return this.directives;
  }

}
