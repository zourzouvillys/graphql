package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;

@Value.Immutable(copy = true)
public abstract class GQLParameterableFieldDeclaration implements GQLFieldDeclaration {

  public abstract List<GQLArgumentDefinition> args();

  public abstract GQLParameterableFieldDeclaration withArgs(GQLArgumentDefinition... elements);

  public abstract GQLParameterableFieldDeclaration withArgs(Iterable<? extends GQLArgumentDefinition> elements);

  @Override
  public abstract GQLParameterableFieldDeclaration withName(String ref);

  @Override
  public abstract GQLParameterableFieldDeclaration withDescription(String ref);

  @Override
  public abstract GQLParameterableFieldDeclaration withType(GQLTypeReference ref);

  @Override
  public abstract GQLParameterableFieldDeclaration withDeprecationReason(String ref);

  @Override
  public abstract GQLParameterableFieldDeclaration withDirectives(GQLDirective... ref);

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("GQLField(");
    sb
        .append(this.name())
        .append(": ")
        .append(this.type());

    if (!this.directives().isEmpty()) {
      sb.append(", fieldDirectives = ");
      sb.append(this.directives());
    }
    return sb.append(')').toString();

  }

  public GQLArgumentDefinition arg(final String name) {
    return this.args().stream().filter(arg -> name.equals(arg.name())).findAny().orElse(null);
  }

  public static ImmutableGQLParameterableFieldDeclaration.Builder builder() {
    return ImmutableGQLParameterableFieldDeclaration.builder();
  }

}
