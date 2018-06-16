package io.zrz.graphql.zulu.schema;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.zulu.annotations.GQLContext;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLOutputExtension;
import io.zrz.graphql.zulu.annotations.GQLOutputType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableType;

@GQLOutputType(name = "__Schema")
@GQLDocumentation("schema")
public class GQLSchema {

  private ExecutableSchema schema;

  public GQLSchema(ExecutableSchema schema) {
    this.schema = schema;
  }

  public GQLSchemaType queryType() {
    return schema.rootType(GQLOpType.Query).map(type -> new GQLSchemaType(type)).orElse(null);
  }

  public GQLSchemaType mutationType() {
    return schema.rootType(GQLOpType.Mutation).map(type -> new GQLSchemaType(type)).orElse(null);
  }

  public GQLSchemaType subscriptionType() {
    return schema.rootType(GQLOpType.Subscription).map(type -> new GQLSchemaType(type)).orElse(null);
  }

  public List<GQLSchemaType> types() {
    return schema.types()
        .filter(type -> !type.typeName().startsWith("__"))
        .filter(type -> !isBuiltin(type))
        .map(type -> new GQLSchemaType(type))
        .collect(Collectors.toList());
  }

  private boolean isBuiltin(ExecutableType type) {
    switch (type.logicalKind()) {
      case SCALAR:
        switch (type.typeName()) {
          case "String":
          case "Int":
          case "int":
          case "Boolean":
          case "Double":
            return true;
        }
        break;
      case ENUM:
      case INPUT:
      case INTERFACE:
      case OUTPUT:
      case UNION:
      default:
        break;
    }
    return type.typeName().startsWith("__");
  }

  public List<GQLSchemaDirective> directives() {
    return Collections.emptyList();
  }

  /**
   * the magic extensions foe __typename
   * 
   * @param instance
   * @return
   */

  @GQLOutputExtension
  public static String __typename(Object instance, @GQLContext ExecutableSchema schema) {
    return schema.type(TypeToken.of(instance.getClass())).typeName();
  }

  /**
   * 
   * @param instance
   * @param schema
   * @return
   */

  @GQLOutputExtension
  public static GQLSchema __schema(Object instance, @GQLContext ExecutableSchema schema) {
    return new GQLSchema(schema);
  }

  /**
   * 
   * @param schema
   * @param typeName
   * @return
   */

  @GQLOutputExtension
  public static GQLSchemaType __type(Object instance, @GQLContext ExecutableSchema schema, @GQLField("name") String typeName) {
    ExecutableType type = schema.resolveType(typeName);
    if (type == null) {
      return null;
    }
    return new GQLSchemaType(type);
  }

}
