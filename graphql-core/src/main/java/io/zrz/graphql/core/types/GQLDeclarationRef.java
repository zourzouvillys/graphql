package io.zrz.graphql.core.types;

import java.util.Collections;
import java.util.List;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
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
@Builder(builderClassName = "Builder")
public final class GQLDeclarationRef implements GQLTypeReference, GQLTypeDeclaration {

  private final String name;

  // the reference, set in the schema builder when resolving types.
  private final GQLTypeDeclaration ref;

  @Singular
  private final List<GQLDirective> directives;

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor) {
    return visitor.visitDeclarationRef(this);
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    if (visitor == null) {
      throw new IllegalStateException();
    }
    return this.ref.apply(visitor);
  }

  public GQLTypeDeclaration ref() {
    return this.ref;
  }

  @Override
  public String description() {
    return this.ref.description();
  }

  // toString only ever used for diagnostics!
  @Override
  public String toString() {
    return String.format("*%s", this.name);
  }

  @Override
  public List<GQLDirective> directives() {
    return Collections.emptyList();
  }

  private final GQLSourceLocation location;

  @Override
  public GQLSourceLocation location() {
    return this.location;
  }

}
