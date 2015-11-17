package io.joss.graphql.core.types;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.lang.GQLTypeVisitor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * A reference to an actual type. No modifiers allowed.
 *
 * The type is replaced when the schema is built to an actual reference.
 *
 * @author theo
 *
 */

@Wither
@EqualsAndHashCode
@ToString
@Builder(builderClassName = "Builder")
public final class GQLDeclarationRef implements GQLTypeReference, GQLDeclaration
{

  private final String name;

  // the reference, set in the schema builder when resolving types.
  private final GQLDeclaration ref;

  @Override
  public String name()
  {
    return this.name;
  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor)
  {
    return visitor.visitDeclarationRef(this);
  }

  @Override
  public <R> R apply(final GQLDeclarationVisitor<R> visitor)
  {
    if (visitor == null)
    {
      throw new IllegalStateException();
    }
    return ref.apply(visitor);
  }

  public GQLDeclaration ref()
  {
    return this.ref;
  }

  @Override
  public String description()
  {
    return this.ref.description();
  }

  // toString only ever used for diagnostics!
  @Override
  public String toString()
  {
    return String.format("*%s", name);
  }

}
