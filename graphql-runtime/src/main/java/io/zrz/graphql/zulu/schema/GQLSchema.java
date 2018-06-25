package io.zrz.graphql.zulu.schema;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.zulu.annotations.GQLContext;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLExtension;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableType;

@GQLObjectType(name = "__Schema")
@GQLDocumentation("schema")
public class GQLSchema {

  private static final Logger log = LoggerFactory.getLogger(GQLSchema.class);
  private final ExecutableSchema schema;

  public GQLSchema(final ExecutableSchema schema) {
    this.schema = schema;
  }

  public GQLSchemaType queryType() {
    return this.schema.rootType(GQLOpType.Query).map(type -> new GQLSchemaType(type)).orElse(null);
  }

  public GQLSchemaType mutationType() {
    return this.schema.rootType(GQLOpType.Mutation).map(type -> new GQLSchemaType(type)).orElse(null);
  }

  public GQLSchemaType subscriptionType() {
    return this.schema.rootType(GQLOpType.Subscription).map(type -> new GQLSchemaType(type)).orElse(null);
  }

  public List<GQLSchemaType> types() {
    return this.schema.types()
        .filter(type -> !type.typeName().startsWith("__"))
        .filter(type -> !this.isBuiltin(type))
        .map(type -> new GQLSchemaType(type))
        .collect(Collectors.toList());
  }

  private boolean isBuiltin(final ExecutableType type) {
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
   * the magic extensions for __typename
   *
   * this uses the schema to map the instance to a concrete type.
   *
   * it is possible that a type may not be registered, and all we have is an interface for it. although it's not defined
   * in the graphql spec specifically, it's assumed this is broken so we print a warning.
   *
   * @param instance
   * @return
   */

  @GQLExtension
  public static String __typename(final Object instance, @GQLContext final ExecutableSchema schema) {

    final TypeToken<? extends @NonNull Object> tok = TypeToken.of(instance.getClass());

    final ExecutableType type = schema.type(tok);

    if (type == null) {
      log.warn("couldn't find registered type for {} - falling back to supertypes", tok);
      // look in the supertypes.
      return tok.getTypes()
          .stream()
          .map(a -> schema.type(a))
          .filter(a -> a != null)
          .findFirst()
          .map(x -> x.typeName())
          .orElse(null);
    }

    return type.typeName();
  }

  /**
   *
   * @param instance
   * @param schema
   * @return
   */

  @GQLExtension
  public static GQLSchema __schema(final Object instance, @GQLContext final ExecutableSchema schema) {
    return new GQLSchema(schema);
  }

  /**
   *
   * @param schema
   * @param typeName
   * @return
   */

  @GQLExtension
  public static GQLSchemaType __type(final Object instance, @GQLContext final ExecutableSchema schema, @GQLField("name") final String typeName) {
    final ExecutableType type = schema.resolveType(typeName);
    if (type == null) {
      return null;
    }
    return new GQLSchemaType(type);
  }

}
