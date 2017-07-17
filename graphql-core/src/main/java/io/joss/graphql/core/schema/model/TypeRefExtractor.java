package io.joss.graphql.core.schema.model;

import io.joss.graphql.core.lang.GQLTypeVisitor;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;

public class TypeRefExtractor<T extends Type> implements GQLTypeVisitor<TypeRef<T>> {

  private final TypeBuilder builder;
  private final TypeRefOption[] options;
  private boolean nullable;

  public TypeRefExtractor(TypeBuilder builder, TypeRefOption[] options) {
    this(builder, options, true);
  }

  public TypeRefExtractor(TypeBuilder builder, TypeRefOption[] options, boolean nullable) {
    this.builder = builder;
    this.options = options;
    this.nullable = nullable;
  }

  @Override
  public TypeRef<T> visitNonNull(GQLNonNullType type) {
    if (!this.nullable) {
      throw new RuntimeException("nested null references");
    }
    return type.type().apply(new TypeRefExtractor<T>(this.builder, this.options, false));
  }

  @Override
  public TypeRef<T> visitList(GQLListType type) {
    return new GenericTypeRef<>(type.type().apply(new TypeRefExtractor<T>(this.builder, this.options)), this.nullable);
  }

  @Override
  public TypeRef<T> visitDeclarationRef(GQLDeclarationRef type) {
    final T reftype = this.builder.lookup(type.name());
    for (final TypeRefOption opt : this.options) {
      switch (opt) {
        case InputCompatible:
          if (!(reftype instanceof InputCompatibleType)) {
            throw new InvalidTypeForInputFieldException(reftype);
          }
          break;
        default:
          throw new RuntimeException("Unsupported Option");
      }
    }
    return new SimpleTypeRef<>(reftype, this.nullable);
  }

}
