package io.joss.graphql.core.doc;

import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@EqualsAndHashCode
@Builder(builderClassName = "Builder")
@Wither
public final class GQLOperationDefinition implements GQLDefinition {

  /**
   * The operation type: Query, Mutation, or Subscription.
   */

  private final GQLOpType type;

  /**
   * The (optional) name of the operation.
   */

  private final String name;

  /**
   * variables provided to the operation. e.g, the "$input: MyType!" in "query
   * moo($input: MyType!)"
   */

  @Singular
  private final List<GQLVariableDefinition> vars;

  /**
   * directive attached to this operation.
   */

  @Singular
  private final List<GQLDirective> directives;

  /**
   * the selections in the query.
   */

  @Singular
  private final List<GQLSelection> selections;

  // ---

  public GQLOpType type() {
    return this.type;
  }

  public String name() {
    return this.name;
  }

  public List<GQLVariableDefinition> vars() {
    return this.vars;
  }

  public List<GQLDirective> directives() {
    return this.directives;
  }

  public List<GQLSelection> selections() {
    return this.selections;
  }

  @Override
  public <R> R apply(final GQLDefinitionVisitor<R> visitor) {
    return visitor.visitOperation(this);
  }

}
