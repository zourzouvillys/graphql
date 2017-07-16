package io.joss.graphql.core.schema;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Strings;

import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLExtendableTypeDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.joss.graphql.core.schema.model.EnumType;
import io.joss.graphql.core.schema.model.InputField;
import io.joss.graphql.core.schema.model.InputType;
import io.joss.graphql.core.schema.model.ObjectField;
import io.joss.graphql.core.schema.model.ObjectType;
import io.joss.graphql.core.schema.model.Type;
import io.joss.graphql.core.utils.AbstractDefaultTypeDeclarationVisitor;
import io.joss.graphql.core.utils.FunctionalTypeDeclVisitor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class TypeBuilder extends AbstractDefaultTypeDeclarationVisitor<Void> {

  private final String name;

  public TypeBuilder(String name) {
    this.name = name;
  }

  private static Void mixedTypeException(Collector<? extends GQLTypeDeclaration> collector, GQLTypeDeclaration type) {
    throw new DiffereringTypeException(
        type.name(),
        String.format("Mixed Types while processing %s: %s", collector, type.getClass().getSimpleName()));
  }

  //
  // --------------------------------
  //

  private InputType buildInputType(Collector<GQLInputTypeDeclaration> collector) {

    if (collector.decl == null) {
      throw new MissingTypeToExtendException(this.name, String.format("missing main decl of extended type '%s'", this.name));
    }

    final InputType.InputTypeBuilder b = InputType.builder();

    if (collector.decl.description() != null) {
      b.description(Strings.emptyToNull(collector.decl.description()));
    }

    collector.decl.fields().stream().map(InputField::new).forEach(b::field);

    for (final GQLInputTypeDeclaration ext : collector.extensions) {
      if (ext.description() != null) {
        b.description(Strings.emptyToNull(ext.description()));
      }
      ext.fields().stream().map(InputField::new).forEach(b::field);
    }

    b.name(this.name);

    return b.build();

  }

  public ObjectType buildObjectType(Collector<GQLObjectTypeDeclaration> collector) {

    if (collector.decl == null) {
      throw new MissingTypeToExtendException(this.name, String.format("missing main decl of extended type '%s'", this.name));
    }

    final ObjectType.ObjectTypeBuilder b = ObjectType.builder();

    if (collector.decl.description() != null) {
      b.description(Strings.emptyToNull(collector.decl.description()));
    }

    collector.decl.fields().stream().map(ObjectField::new).forEach(b::field);

    for (final GQLObjectTypeDeclaration ext : collector.extensions) {
      if (ext.description() != null) {
        b.description(Strings.emptyToNull(ext.description()));
      }
      ext.fields().stream().map(ObjectField::new).forEach(b::field);
    }

    b.name(this.name);

    return b.build();

  }

  public EnumType buildEnumType(Collector<GQLEnumDeclaration> collector) {
    return new EnumType(this.name);
  }

  //
  // --------------------------------
  //

  private static class Collector<T extends GQLExtendableTypeDeclaration> {

    T decl;
    List<T> extensions = new LinkedList<>();

    Void add(T item) {
      if (item.isExtension()) {
        this.extensions.add(item);
      } else if (this.decl != null) {
        throw new DuplicateDeclarationException(this.decl.name(), String.format("duplicate decl of %s", this.decl.name()));
      } else {
        this.decl = item;
      }
      return null;
    }

  }

  private Builder builder;

  //

  @Value
  @RequiredArgsConstructor
  private class Builder {

    GQLTypeDeclarationVisitor<Void> visitor;
    Supplier<Type> apply;

  }

  public class Initializer extends AbstractDefaultTypeDeclarationVisitor<Builder> {

    @Override
    public Builder visitInput(GQLInputTypeDeclaration type) {
      final Collector<GQLInputTypeDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.inputType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeBuilder.this.buildInputType(collector));
    }

    @Override
    public Builder visitObject(GQLObjectTypeDeclaration type) {
      final Collector<GQLObjectTypeDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.objectType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeBuilder.this.buildObjectType(collector));
    }

    @Override
    public Builder visitEnum(GQLEnumDeclaration type) {
      final Collector<GQLEnumDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.enumType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeBuilder.this.buildEnumType(collector));
    }

    @Override
    protected Builder visitDefault(GQLTypeDeclaration type) {
      throw new NotImplementedException(String.format("Unsupported type '%s'", type.getClass().getSimpleName()));
    }

  }

  @Override
  protected Void visitDefault(GQLTypeDeclaration type) {
    if (this.builder == null) {
      this.builder = type.apply(new Initializer());
    }
    return type.apply(this.builder.visitor);
  }

  public Type build() {
    return this.builder.apply.get();
  }

}
