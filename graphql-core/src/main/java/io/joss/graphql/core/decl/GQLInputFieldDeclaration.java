package io.joss.graphql.core.decl;

import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.value.GQLValue;
import lombok.Builder;
import lombok.experimental.Wither;

@Wither
@Builder(builderClassName = "Builder")
public class GQLInputFieldDeclaration implements GQLFieldDeclaration {

  private final String name;
  private final String description;

  private final GQLTypeReference type;

  private final String deprecationReason;

  private final GQLValue defaultValue;

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

  public GQLValue defaultValue() {
    return this.defaultValue;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("GQLInputField(");
    sb.append(this.name()).append(": ").append(this.type());
    sb.append(')');
    if (this.defaultValue != null) {
      sb.append(" = ");
      sb.append(this.defaultValue);
    }
    return sb.toString();

  }

}
