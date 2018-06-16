package io.zrz.graphql.core.types;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.lang.GQLTypeVisitor;

/**
 * A reference to an actual type. No modifiers allowed.
 *
 * The type is replaced when the schema is built to an actual reference.
 *
 * @author theo
 *
 */

@Value.Immutable(copy = true)
public abstract class GQLDeclarationRef implements GQLTypeReference, GQLTypeDeclaration {

  // the reference, set in the schema builder when resolving types.
  public abstract @Nullable GQLTypeDeclaration ref();

  public abstract GQLDeclarationRef withRef(GQLTypeDeclaration ref);

  @Override
  public <R> R apply(final GQLTypeVisitor<R> visitor) {
    return visitor.visitDeclarationRef(this);
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    if (visitor == null) {
      throw new IllegalStateException();
    }

    @NonNull
    final GQLTypeDeclaration ref = Objects.requireNonNull(this.ref());

    return ref.apply(visitor);

  }

  @Override
  public GQLTypeRefKind typeRefKind() {
    return GQLTypeRefKind.DECL;
  }

  @Override
  public GQLTypeDeclKind typeKind() {
    return null;
  }

  // toString only ever used for diagnostics!
  @Override
  public String toString() {
    return String.format("*%s", this.name());
  }

  public static ImmutableGQLDeclarationRef.Builder builder() {
    return ImmutableGQLDeclarationRef.builder();
  }

}
