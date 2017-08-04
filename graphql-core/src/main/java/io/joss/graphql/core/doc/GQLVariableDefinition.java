package io.joss.graphql.core.doc;

import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.types.GQLTypes;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder
public final class GQLVariableDefinition {

  /**
   * The name of this variable, without the leading '$'.
   */

  private final String name;

  /**
   * The type. Note this will initially be a GQLTypeRef until it's resolve.
   */

  private final GQLTypeReference type;

  /**
   * The default value, if there is one.
   */

  private final GQLValue defaultValue;

  /**
   * the defined name for this variable definition.
   */

  public String name() {
    return this.name;
  }

  /**
   * the declared type of this variable.
   */

  public GQLTypeReference type() {
    return this.type;
  }

  /**
   * The default value declared for this variable.
   */

  public GQLValue defaultValue() {
    return this.defaultValue;
  }

  /**
   * Creates an integer variable definition.
   */

  public static GQLVariableDefinition intVar(final String name, final long value) {
    return builder().name(name).type(GQLTypes.intType()).defaultValue(GQLValues.intValue(value)).build();
  }

}
