package io.zrz.graphql.core.value;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * A reference to a variable.
 *
 * Until the document is resolved, we don't know the type of this variable.
 *
 */

@EqualsAndHashCode
@ToString
@Builder
@Wither
public final class GQLVariableRef implements GQLValue {

  private final String name;

  /**
   * The name of the variable referenced.
   *
   * @return
   */

  public String name() {
    return this.name;
  }

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitVarValue(this);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.VariableRef;
  }

}
