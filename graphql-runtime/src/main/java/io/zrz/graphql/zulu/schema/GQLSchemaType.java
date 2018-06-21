package io.zrz.graphql.zulu.schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import io.zrz.graphql.core.types.GQLTypeKind;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.graphql.zulu.executable.ExecutableType;

@GQLObjectType(name = "__Type")
public class GQLSchemaType {

  private final ExecutableType type;
  private int arity;

  public GQLSchemaType(ExecutableType type) {
    this(type, 0);
  }

  public GQLSchemaType(ExecutableType type, int arity) {
    this.type = Objects.requireNonNull(type);
    this.arity = arity;
  }

  // kind: __TypeKind!
  public @NonNull GQLTypeKind kind() {

    if (this.arity > 0) {
      return GQLTypeKind.LIST;
    }

    switch (type.logicalKind()) {
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
        throw new IllegalArgumentException(type.logicalKind().name());
    }

  }

  // name: String
  public String name() {
    if (this.arity > 0)
      return null;
    return type.typeName();
  }

  // description: String
  public String description() {
    return type.documentation();
  }

  // # OBJECT and INTERFACE only
  // fields(includeDeprecated: Boolean = false): [__Field!]
  public List<@NonNull GQLSchemaField> fields(boolean includeDeprecated) {
    if (type.logicalKind() != LogicalTypeKind.OUTPUT) {
      return null;
    }
    return ((ExecutableOutputType) type)
        .fields()
        .values()
        .stream()
        .filter(field -> !field.fieldName().startsWith("__"))
        .map(x -> new GQLSchemaField(x))
        .collect(Collectors.toList());
  }

  //
  // # OBJECT only
  // interfaces: [__Type!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaType> interfaces() {
    if (this.type.logicalKind() != LogicalTypeKind.OUTPUT)
      return null;
    return Collections.emptyList();
  }

  //
  // # INTERFACE and UNION only
  // possibleTypes: [__Type!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaType> possibleTypes() {
    if (this.type.logicalKind() != LogicalTypeKind.INTERFACE && type.logicalKind() != LogicalTypeKind.UNION)
      return null;
    return Collections.emptyList();
  }

  //
  // # ENUM only
  // enumValues(includeDeprecated: Boolean = false): [__EnumValue!]
  public List<io.zrz.graphql.zulu.schema.GQLSchemaEnumValue> enumValues(boolean includeDeprecated) {
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

    if (this.arity > 0) {
      return new GQLSchemaType(this.type, this.arity - 1);
    }

    return null;
  }

}
