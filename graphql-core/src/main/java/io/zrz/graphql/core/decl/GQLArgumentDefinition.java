package io.zrz.graphql.core.decl;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.value.GQLValue;

/**
 * An argument definition.
 *
 * @author theo
 */

@Value.Style(allowedClasspathAnnotations = { Override.class })
@Value.Immutable(copy = true)
public interface GQLArgumentDefinition {

  String name();

  @Nullable
  String description();

  List<GQLDirective> directives();

  GQLTypeReference type();

  GQLArgumentDefinition withType(GQLTypeReference value);

  Optional<GQLValue> defaultValue();

  static ImmutableGQLArgumentDefinition.Builder builder() {
    return ImmutableGQLArgumentDefinition.builder();
  }

}
