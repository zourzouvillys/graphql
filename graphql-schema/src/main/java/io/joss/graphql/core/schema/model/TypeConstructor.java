package io.joss.graphql.core.schema.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLExtendableTypeDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.joss.graphql.core.schema.DiffereringTypeException;
import io.joss.graphql.core.schema.DuplicateDeclarationException;
import io.joss.graphql.core.schema.MissingTypeToExtendException;
import io.joss.graphql.core.schema.NotImplementedException;
import io.joss.graphql.core.utils.AbstractDefaultTypeDeclarationVisitor;
import io.joss.graphql.core.utils.FunctionalTypeDeclVisitor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

public class TypeConstructor extends AbstractDefaultTypeDeclarationVisitor<Void> {

  private final String name;
  private final Model model;
  private final TypeBuilder typebuilder;

  public TypeConstructor(TypeBuilder builder, Model model, String name) {
    this.typebuilder = builder;
    this.name = name;
    this.model = model;
  }

  private static Void mixedTypeException(Collector<? extends GQLTypeDeclaration> collector, GQLTypeDeclaration type) {
    throw new DiffereringTypeException(
        type.name(),
        String.format("Mixed Types while processing %s: %s", collector, type.getClass().getSimpleName()));
  }

  private static Void unsupportedException(GQLScalarTypeDeclaration decl, GQLTypeDeclaration type) {
    throw new DiffereringTypeException(
        type.name(),
        String.format("Can't extend type %s: %s", decl, type.getClass().getSimpleName()));
  }

  //
  // --------------------------------
  //

  private InputType buildInputType(Collector<GQLInputTypeDeclaration> collector) {
    if (collector.decl == null) {
      throw new MissingTypeToExtendException(this.name, String.format("missing main decl of extended type '%s'", this.name));
    }
    return new InputType(this.typebuilder, this.model, this.name, collector.decl, collector.extensions);
  }

  public ObjectType buildObjectType(Collector<GQLObjectTypeDeclaration> collector) {
    if (collector.decl == null) {
      throw new MissingTypeToExtendException(this.name, String.format("missing main decl of extended type '%s'", this.name));
    }
    return new ObjectType(this.typebuilder, this.model, this.name, collector.decl, collector.extensions);
  }

  public InterfaceType buildInterfaceType(Collector<GQLInterfaceTypeDeclaration> collector) {
    if (collector.decl == null) {
      throw new MissingTypeToExtendException(this.name, String.format("missing main decl of extended type '%s'", this.name));
    }
    return new InterfaceType(this.typebuilder, this.model, this.name, collector.decl, collector.extensions);
  }

  public EnumType buildEnumType(Collector<GQLEnumDeclaration> collector) {
    return new EnumType(this.typebuilder, this.model, this.name, collector.decl, collector.extensions);
  }

  public ScalarType buildScalarType(GQLScalarTypeDeclaration collector) {
    return new ScalarType(this.typebuilder, this.model, this.name, collector);
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
    Supplier<AbstractType> apply;

  }

  public class Initializer extends AbstractDefaultTypeDeclarationVisitor<Builder> {

    @Override
    public Builder visitInput(GQLInputTypeDeclaration type) {
      final Collector<GQLInputTypeDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.inputType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeConstructor.this.buildInputType(collector));
    }

    @Override
    public Builder visitObject(GQLObjectTypeDeclaration type) {
      final Collector<GQLObjectTypeDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.objectType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeConstructor.this.buildObjectType(collector));
    }

    @Override
    public Builder visitInterface(GQLInterfaceTypeDeclaration type) {
      final Collector<GQLInterfaceTypeDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.interfaceType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeConstructor.this.buildInterfaceType(collector));
    }

    @Override
    public Builder visitEnum(GQLEnumDeclaration type) {
      final Collector<GQLEnumDeclaration> collector = new Collector<>();
      return new Builder(
          FunctionalTypeDeclVisitor.enumType(in -> collector.add(in), in -> mixedTypeException(collector, in)),
          () -> TypeConstructor.this.buildEnumType(collector));
    }

    @Override
    public Builder visitScalar(GQLScalarTypeDeclaration type) {
      final AtomicReference<GQLScalarTypeDeclaration> ref = new AtomicReference<>();
      return new Builder(
          FunctionalTypeDeclVisitor.scalarType(in -> {
            ref.set(in);
            return null;
          }, in -> unsupportedException(ref.get(), in)),
          () -> TypeConstructor.this.buildScalarType(ref.get()));
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

  public AbstractType build() {
    return this.builder.apply.get();
  }

  private final Map<String, AbstractType> registered = new HashMap<>();

  void register(AbstractType type, String name) {
    this.registered.put(name, type);
  }

}
