package io.joss.graphql.core.types;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;
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
public final class GQLDeclarationRef implements GQLTypeReference, GQLTypeDeclaration
{

  private final String name;

  // the reference, set in the schema builder when resolving types.
  private final GQLTypeDeclaration ref;

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
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor)
  {
    if (visitor == null)
    {
      throw new IllegalStateException();
    }
    return ref.apply(visitor);
  }

  public GQLTypeDeclaration ref()
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
