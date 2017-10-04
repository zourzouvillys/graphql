package io.zrz.graphql.core.decl;

import java.util.List;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.Wither;

@Wither
@Builder(builderClassName = "Builder")
public final class GQLParameterableFieldDeclaration implements GQLFieldDeclaration {

  private final String name;
  private final String description;

  private final GQLTypeReference type;

  private final String deprecationReason;

  @Singular
  private final List<GQLArgumentDefinition> args;

  @Singular
  private final List<GQLDirective> directives;

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String description() {
    return this.description;
  }

  @Override
  public GQLTypeReference type() {
    return this.type;
  }

  @Override
  public String deprecationReason() {
    return this.deprecationReason;
  }

  public List<GQLArgumentDefinition> args() {
    return this.args;
  }

  public List<GQLDirective> directives() {
    return this.directives;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("GQLField(");
    sb.append(this.name()).append(": ").append(this.type());
    return sb.append(')').toString();

  }

  public GQLArgumentDefinition arg(String name) {
    return this.args.stream().filter(arg -> name.equals(arg.name())).findAny().orElse(null);
  }

}
