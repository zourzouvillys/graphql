package io.joss.graphql.core.lang;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLTypeReference;
import io.joss.graphql.core.types.GQLTypes;

/**
 * A compiled type registry. This is used to resolve type references and provide
 * other type lookups.
 *
 * @author theo
 *
 */

public class GQLTypeRegistry {

  private final Map<String, GQLTypeDeclaration> types;

  public GQLTypeRegistry(final Map<String, GQLTypeDeclaration> types) {
    types.values().forEach(Objects::requireNonNull);
    this.types = types;
  }

  public Collection<? extends GQLTypeDeclaration> types() {
    return this.types.values();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    this.types.forEach((k, v) -> sb.append(k).append('=').append(v.toString()).append('\n'));
    return sb.toString();
  }

  public GQLScalarTypeDeclaration scalar(final String name) {
    return (GQLScalarTypeDeclaration) this.types.get(name);
  }

  public GQLObjectTypeDeclaration objectType(String name) {
    return (GQLObjectTypeDeclaration) this.types.get(name);
  }

  public GQLUnionTypeDeclaration union(String name) {
    return (GQLUnionTypeDeclaration) this.types.get(name);
  }

  public GQLTypeDeclaration resolve(GQLDeclarationRef ref) {
    return this.types.get(ref.name());
  }

  public <V> void apply(GQLTypeDeclarationVisitor<V> visitor) {
    this.types.values().forEach(type -> {
      type.apply(visitor);
    });
  }

  public GQLTypeDeclaration decl(String name) {
    final GQLTypeDeclaration ret = this.types.get(name);
    if (ret == null) {
      throw new IllegalStateException(String.format("Unable to find type '%s'.", name));
    }
    return ret;
  }

  public GQLTypeReference ref(String name) {
    return GQLTypes.ref(this.decl(name));
  }

}
