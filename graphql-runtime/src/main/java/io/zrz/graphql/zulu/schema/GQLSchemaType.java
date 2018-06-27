package io.zrz.graphql.zulu.schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.graphql.core.types.GQLTypeKind;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableInterfaceType;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;

@GQLObjectType(name = "__Type")
public class GQLSchemaType {

  private final ExecutableType type;
  private final int arity;
  private final boolean nullable;
  private final String typeName;

  public GQLSchemaType(final ExecutableType type) {
    this(type, 0, true);
  }

  public GQLSchemaType(final ExecutableType type, final int arity, final boolean nullable) {
    this.type = Objects.requireNonNull(type);
    this.typeName = type.typeName();
    this.arity = arity;
    this.nullable = nullable;
  }

  public GQLSchemaType(final ExecutableType type, final String typeName, final int arity, final boolean nullable) {
    this.type = Objects.requireNonNull(type);
    this.typeName = typeName;
    this.arity = arity;
    this.nullable = nullable;
  }

  public GQLSchemaType(final ExecutableTypeUse fieldType) {
    this.typeName = fieldType.logicalType();
    this.type = fieldType.type();
    this.arity = fieldType.arity();
    this.nullable = fieldType.isNullable();
  }

  // kind: __TypeKind!
  public @NonNull GQLTypeKind kind() {

    if (!this.nullable) {
      return GQLTypeKind.NON_NULL;
    }

    if (this.arity > 0) {
      return GQLTypeKind.LIST;
    }

    switch (this.type.logicalKind()) {
      case ENUM:
        return GQLTypeKind.ENUM;
      case INPUT:
        return GQLTypeKind.INPUT_OBJECT;
      case INTERFACE:
        return GQLTypeKind.INTERFACE;
      case OUTPUT:
        return GQLTypeKind.OBJECT;
      case SCALAR:
        return GQLTypeKind.SCALAR;
      case UNION:
        return GQLTypeKind.UNION;
      default:
        throw new IllegalArgumentException(this.type.logicalKind().name());
    }

  }

  // name: String
  public String name() {
    if (this.arity > 0)
      return null;
    return this.typeName;
  }

  // description: String
  public String description() {
    return this.type.documentation();
  }

  // # OBJECT and INTERFACE only
  // fields(includeDeprecated: Boolean = false): [__Field!]

  public List<@NonNull GQLSchemaField> fields(final Boolean includeDeprecated) {

    switch (this.type.logicalKind()) {

      case OUTPUT:

        return ((ExecutableOutputType) this.type)
            .fields()
            .values()
            .stream()
            .filter(field -> !field.fieldName().startsWith("__"))
            .map(x -> new GQLSchemaField(x))
            .collect(Collectors.toList());

      case INTERFACE:
        return ((ExecutableInterfaceType) this.type)
            .fields()
            .values()
            .stream()
            .filter(field -> !field.fieldName().startsWith("__"))
            .map(x -> new GQLSchemaField(x))
            .collect(Collectors.toList());

      default:
        return null;

    }

  }

  //
  // # OBJECT only
  // interfaces: [__Type!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaType> interfaces() {

    switch (this.type.logicalKind()) {
      case OUTPUT:
        return ((ExecutableOutputType) this.type)
            .interfaces()
            .stream()
            .map(x -> new GQLSchemaType(x))
            .collect(Collectors.toList());
      default:
        return null;
    }

  }

  //
  // # INTERFACE and UNION only
  // possibleTypes: [__Type!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaType> possibleTypes() {
    if (this.type.logicalKind() != LogicalTypeKind.INTERFACE && this.type.logicalKind() != LogicalTypeKind.UNION)
      return null;
    return Collections.emptyList();
  }

  //
  // # ENUM only
  // enumValues(includeDeprecated: Boolean = false): [__EnumValue!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaEnumValue> enumValues(final boolean includeDeprecated) {
    if (this.type.logicalKind() != LogicalTypeKind.ENUM)
      return null;
    return Collections.emptyList();
  }

  //
  // # INPUT_OBJECT only
  // inputFields: [__InputValue!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaInputValue> inputFields() {
    if (this.type.logicalKind() != LogicalTypeKind.INPUT)
      return null;
    return Collections.emptyList();
  }

  //
  // # NON_NULL and LIST only
  // ofType: __Type
  public GQLSchemaType ofType() {

    if (!this.nullable) {
      return new GQLSchemaType(this.type, this.typeName, this.arity, true);
    }

    if (this.arity > 0) {
      return new GQLSchemaType(this.type, this.typeName, this.arity - 1, true);
    }

    return null;
  }

}
