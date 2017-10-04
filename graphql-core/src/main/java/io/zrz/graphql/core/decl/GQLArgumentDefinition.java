package io.zrz.graphql.core.decl;

import java.util.List;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;
import io.zrz.graphql.core.value.GQLValue;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * An argument definition.
 *
 * @author theo
 */

@Wither
@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
public final class GQLArgumentDefinition {

  @NonNull
  private final String name;
  private final String description;

  @Singular
  private final List<GQLDirective> directives;

  @NonNull
  private final GQLTypeReference type;

  private final GQLValue defaultValue;

  public String name() {
    return this.name;
  }

  public String description() {
    return this.description;
  }

  public GQLTypeReference type() {
    return this.type;
  }

  public GQLValue defaultValue() {
    return this.defaultValue;
  }

  public List<GQLDirective> directives() {
    return this.directives;
  }

}
