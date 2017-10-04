package io.zrz.graphql.core.doc;

import java.util.List;

import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
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
   * directive attached to this variable.
   */

  @Singular
  private final List<GQLDirective> directives;

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
   * the directives
   */

  public List<GQLDirective> directives() {
    return this.directives;
  }

  /**
   * Creates an integer variable definition.
   */

  public static GQLVariableDefinition intVar(final String name, final long value) {
    return builder().name(name).type(GQLTypes.intType()).defaultValue(GQLValues.intValue(value)).build();
  }

}
