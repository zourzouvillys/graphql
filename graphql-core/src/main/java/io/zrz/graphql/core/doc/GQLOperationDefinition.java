package io.zrz.graphql.core.doc;

import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.parser.GQLSourceRange;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
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

  public abstract Optional<GQLSourceRange> range();

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
