package io.zrz.graphql.core.doc;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;

@Value.Immutable(copy = true)
public abstract class GQLVariableDefinition {

  /**
   * The name of this variable, without the leading '$'.
   */

  public abstract String name();

  /**
   * The type. Note this will initially be a GQLTypeRef until it's resolve.
   */

  public abstract GQLTypeReference type();

  /**
   * The default value declared for this variable.
   */

  @Nullable
  public abstract GQLValue defaultValue();

  /**
   * the directives
   */

  public abstract List<GQLDirective> directives();

  /**
   * Creates an integer variable definition.
   */

  public static GQLVariableDefinition intVar(final String name, final long value) {
    return builder().name(name).type(GQLTypes.intType()).defaultValue(GQLValues.intValue(value)).build();
  }

  public static ImmutableGQLVariableDefinition.Builder builder() {
    return ImmutableGQLVariableDefinition.builder();
  }

}
