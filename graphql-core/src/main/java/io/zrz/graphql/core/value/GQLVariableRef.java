package io.zrz.graphql.core.value;

import org.immutables.value.Value;

/**
 * A reference to a variable.
 *
 * Until the document is resolved, we don't know the type of this variable.
 *
 */

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLVariableRef implements GQLValue {

  /**
   * The name of the variable referenced.
   *
   * @return
   */

  public abstract String name();

  @Override
  public <R> R apply(final GQLValueVisitor<R> visitor) {
    return visitor.visitVarValue(this);
  }

  @Override
  public GQLValueType type() {
    return GQLValueType.VariableRef;
  }

  public static ImmutableGQLVariableRef.Builder builder() {
    return ImmutableGQLVariableRef.builder();
  }

}
