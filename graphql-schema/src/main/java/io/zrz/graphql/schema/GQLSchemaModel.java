package io.zrz.graphql.schema;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

import io.vavr.collection.List;
import io.zrz.graphql.core.decl.GQLDeclaration;
import io.zrz.graphql.core.decl.GQLDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLSchemaDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;

public final class GQLSchemaModel {

  private final List<GQLDeclaration> decls;

  private GQLSchemaModel() {
    this.decls = List.empty();
  }

  private GQLSchemaModel(final List<GQLDeclaration> decls) {
    this.decls = decls;
  }

  public GQLSchemaModel mergeWith(final GQLSchemaModel b) {
    return new GQLSchemaModel(this.decls.appendAll(b.decls));
  }

  public GQLSchemaModel withType(final GQLTypeDeclaration type) {
    return new GQLSchemaModel(this.decls.push(type));
  }

  public GQLSchemaModel withSchema(final GQLSchemaDeclaration schema) {
    return new GQLSchemaModel(this.decls.push(schema));
  }

  public void forEach(final Consumer<GQLDeclaration> action) {
    this.decls.forEach(action);
  }

  public GQLTypeDeclaration symbol(final String typeName) {
    return this.symbol(typeName, GQLTypeDeclaration.class);
  }

  public <T extends GQLTypeDeclaration> T symbol(final String typeName, final Class<T> klass) {
    return this.decls
        .filter(klass::isInstance)
        .map(klass::cast)
        .filter(e -> e.name().equals(typeName))
        .get();
  }

  // helpers

  public static GQLSchemaModel fromDecls(final Stream<GQLDeclaration> decls) {
    return decls.reduce(new GQLSchemaModel(), (a, b) -> b.apply(new AddToModel(a)), (a, b) -> a.mergeWith(b));
  }

  private static class AddToModel implements GQLDeclarationVisitor<GQLSchemaModel> {

    private final GQLSchemaModel model;

    public AddToModel(final GQLSchemaModel model) {
      this.model = model;
    }

    @Override
    public GQLSchemaModel visitTypeDeclaration(final GQLTypeDeclaration typedecl) {
      return this.model.withType(typedecl);
    }

    @Override
    public GQLSchemaModel visitSchemaDeclaration(final GQLSchemaDeclaration schema) {
      return this.model.withSchema(schema);
    }

  }

  public @NonNull GQLSchemaDeclaration schema() {
    return this.decls
        .filter(GQLSchemaDeclaration.class::isInstance)
        .map(GQLSchemaDeclaration.class::cast)
        .get();
  }

  public @NonNull GQLObjectTypeDeclaration mutationType() {
    return this.symbol(this.schema().entries().get("mutation").name(), GQLObjectTypeDeclaration.class);
  }

}
