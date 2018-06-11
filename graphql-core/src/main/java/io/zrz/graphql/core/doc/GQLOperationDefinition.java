package io.zrz.graphql.core.doc;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable(copy = true)
public abstract class GQLOperationDefinition implements GQLDefinition {

  /**
   * The operation type: Query, Mutation, or Subscription.
   */

  public abstract @Nullable GQLOpType type();

  /**
   * The (optional) name of the operation.
   */
  public abstract @Nullable String name();

  /**
   * variables provided to the operation. e.g, the "$input: MyType!" in "query moo($input: MyType!)"
   */

  public abstract List<GQLVariableDefinition> vars();

  /**
   * directive attached to this operation.
   */

  public abstract List<GQLDirective> directives();

  /**
   * the selections in the query.
   */

  public abstract List<GQLSelection> selections();

  /**
   * 
   */

  @Override
  public <R> R apply(final GQLDefinitionVisitor<R> visitor) {
    return visitor.visitOperation(this);
  }

  public static ImmutableGQLOperationDefinition.Builder builder() {
    return ImmutableGQLOperationDefinition.builder();
  }

}
