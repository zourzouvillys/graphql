package io.zrz.graphql.core.doc;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLFragmentDefinition implements GQLDefinition {

  public abstract String name();

  public abstract GQLDeclarationRef namedType();

  public abstract List<GQLDirective> directives();

  public abstract List<GQLSelection> selections();

  @Override
  public <R> R apply(final GQLDefinitionVisitor<R> visitor) {
    return visitor.visitFragment(this);
  }

  public static ImmutableGQLFragmentDefinition.Builder builder() {
    return ImmutableGQLFragmentDefinition.builder();
  }

}
