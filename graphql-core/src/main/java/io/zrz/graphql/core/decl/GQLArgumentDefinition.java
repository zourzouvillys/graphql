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

@Value.Immutable(copy = true)
public interface GQLArgumentDefinition {

  abstract String name();

  @Nullable
  abstract String description();

  abstract List<GQLDirective> directives();

  abstract GQLTypeReference type();

  ImmutableGQLArgumentDefinition withType(GQLTypeReference value);

  abstract Optional<GQLValue> defaultValue();

  public static ImmutableGQLArgumentDefinition.Builder builder() {
    return ImmutableGQLArgumentDefinition.builder();
  }

}
