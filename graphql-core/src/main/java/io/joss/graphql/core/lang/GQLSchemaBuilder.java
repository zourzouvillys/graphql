package io.joss.graphql.core.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLSchemaDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclaration;

/**
 * Provides utilities for building a schema. This involves resolving type
 * references, peforming type validations, etc.
 *
 * @author theo
 *
 */

public class GQLSchemaBuilder {

  private final GQLSchemaDeclaration schema = null;
  private final Map<String, GQLTypeDeclaration> types = new HashMap<>();

  public GQLSchemaBuilder add(final GQLDeclaration type) {
    return type.apply(new GQLDeclarationVisitor<GQLSchemaBuilder>() {

      @Override
      public GQLSchemaBuilder visitTypeDeclaration(GQLTypeDeclaration typedecl) {
        return GQLSchemaBuilder.this.add(typedecl);
      }

      @Override
      public GQLSchemaBuilder visitSchemaDeclaration(GQLSchemaDeclaration gqlSchemaDeclaration) {
        // ignored for this
        return null;
      }

    });
  }

  public GQLSchemaBuilder add(final GQLTypeDeclaration type) {

    type.apply(new ValidateDeclarationVisitor());

    if (type.name() == null || type.name().isEmpty()) {
      throw new IllegalArgumentException("empty name");
    } else if (this.types.containsKey(type.name())) {
      throw new DuplicateTypeNameException(type.name());
    }

    this.types.put(type.name(), type);

    return this;

  }

  public GQLSchemaBuilder add(final Collection<? extends GQLDeclaration> type) {
    type.forEach(this::add);
    return this;
  }

  /**
   *
   * @return
   */

  public GQLTypeRegistry build() {

    final Map<String, GQLTypeDeclaration> types = new HashMap<>();

    for (final Map.Entry<String, GQLTypeDeclaration> e : this.types.entrySet()) {
      types.put(e.getKey(), e.getValue().apply(new ResolveReferencesVisitor(this.types)));
    }

    final GQLTypeRegistry reg = new GQLTypeRegistry(types);

    reg.apply(new PostCreationValidator(reg));

    return reg;

  }

}
