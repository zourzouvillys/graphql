package io.zrz.graphql.zulu.executable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.binding.JavaBindingProvider;
import io.zrz.graphql.zulu.binding.JavaBindingType;
import io.zrz.graphql.zulu.binding.JavaBindingUtils;
import io.zrz.graphql.zulu.spi.ExtensionGenerator;

/**
 * builds a schema that is bound to the defined handlers, and exposes a GraphQL compatible model that can be
 * introspected.
 * 
 * the resulting model has all fields and types resolved, and provides a mechanism for binding.
 * 
 * @author theo
 *
 */

public final class ExecutableSchemaBuilder {

  // the binder component is stateful, as it knows types previously registered.
  private JavaBindingProvider binder = new JavaBindingProvider();

  /**
   * the registered operation roots.
   */

  private Map<GQLOperationType, Symbol> operationRoots = new HashMap<>();

  /**
   * 
   */

  private Map<String, Symbol> names = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  private Map<TypeToken<?>, Symbol> types = new HashMap<>();
  private Multimap<LogicalTypeKind, Symbol> kinds = HashMultimap.create();

  public ExecutableSchemaBuilder() {
    this.addScalar(String.class);
    this.addScalar(Boolean.class, "Boolean");
    this.addScalar(Integer.class);
    this.addScalar(Long.class);
    this.addScalar(Double.class);
    this.addScalar(Boolean.TYPE, "Boolean");
    this.addScalar(Integer.TYPE);
    this.addScalar(Long.TYPE);
    this.addScalar(Double.TYPE);
  }

  /**
   * 
   */

  static class Symbol {
    String typeName;
    LogicalTypeKind typeKind;
    TypeToken<?> typeToken;
    JavaBindingType handle;
  }

  /**
   * exposes the query root.
   * 
   * @return
   */

  public ExecutableSchemaBuilder setRootType(GQLOperationType operation, Type klass) {
    return this.setRootType(operation, klass, JavaBindingUtils.autoTypeName(TypeToken.of(klass)));
  }

  /**
   * set the root for an operation.
   * 
   * @param operationType
   * @param klass
   * @param typeName
   * @return
   */

  public ExecutableSchemaBuilder setRootType(GQLOperationType operationType, Type klass, String typeName) {
    TypeToken<?> typeToken = TypeToken.of(klass);
    Preconditions.checkState(!this.operationRoots.containsKey(operationType));
    Symbol symbol = this.registerType(typeToken, typeName, LogicalTypeKind.OUTPUT);
    this.operationRoots.put(operationType, symbol);
    return this;
  }

  private void checkSymbol(TypeToken<?> typeToken, String typeName, LogicalTypeKind typeKind) {

    Symbol symbol = this.names.get(typeName);

    if (symbol != null) {

      if (!typeKind.equals(symbol.typeKind)) {
        throw new IllegalStateException("can't register symbol '" + typeName
            + "' as a " + typeKind + " kind, already " + symbol.typeKind);
      }

      if (!typeToken.equals(symbol.typeToken) && !typeToken.isPrimitive()) {
        throw new IllegalStateException("can't register symbol '" + typeName
            + "' as type " + typeToken + ", already '" + symbol.typeToken + "'");
      }

    }

    Symbol typeSymbol = this.types.get(typeToken);

    if (typeSymbol != null) {

      if (!typeKind.equals(typeSymbol.typeKind)) {
        throw new IllegalStateException("can't register type '" + typeToken
            + "' as a " + typeKind + " type kind already " + typeSymbol.typeKind);
      }

      if (!typeName.equals(typeSymbol.typeName)) {
        throw new IllegalStateException("can't register type '" + typeToken
            + "' as symbol '" + typeName + "', already '" + typeSymbol.typeName + "'");
      }

    }

  }

  private Symbol addSymbol(TypeToken<?> typeToken, String typeName, LogicalTypeKind typeKind, JavaBindingType handle) {

    Symbol symbol = new Symbol();

    symbol.typeName = typeName;
    symbol.typeKind = typeKind;
    symbol.typeToken = typeToken;
    symbol.handle = handle;

    this.names.put(symbol.typeName, symbol);
    this.types.put(symbol.typeToken, symbol);
    this.kinds.put(symbol.typeKind, symbol);

    return symbol;

  }

  private Symbol registerType(TypeToken<?> typeToken, String typeName, LogicalTypeKind typeKind) {
    if (typeName == null) {
      typeName = JavaBindingUtils.autoTypeName(typeToken);
    }
    this.checkSymbol(typeToken, typeName, typeKind);
    if (typeKind.equals(LogicalTypeKind.OUTPUT)) {
      JavaBindingType handle = binder.registerType(typeToken);
      return this.addSymbol(typeToken, typeName, typeKind, handle);
    }
    else {
      return this.addSymbol(typeToken, typeName, typeKind, null);
    }
  }

  /**
   * exposes a java class as a GraphQL output type.
   */

  public ExecutableSchemaBuilder addType(Type klass) {
    registerType(TypeToken.of(klass), null, LogicalTypeKind.OUTPUT);
    return this;
  }

  /**
   * exposes a java class as a GraphQL type.
   */

  public ExecutableSchemaBuilder addType(Type klass, String typeName) {
    registerType(TypeToken.of(klass), typeName, LogicalTypeKind.OUTPUT);
    return this;
  }

  /**
   * exports the given java type as a GraphQL interface.
   */

  public ExecutableSchemaBuilder addInterface(Type klass) {
    registerType(TypeToken.of(klass), null, LogicalTypeKind.INTERFACE);
    return this;
  }

  /**
   * exports the given java type as a GraphQL union.
   */

  public ExecutableSchemaBuilder addUnion(Type klass) {
    registerType(TypeToken.of(klass), null, LogicalTypeKind.UNION);
    return this;
  }

  /**
   * exports the given java enum as a GraphQL enum
   */

  public ExecutableSchemaBuilder addEnum(Type klass) {
    registerType(TypeToken.of(klass), null, LogicalTypeKind.ENUM);
    return this;
  }

  /**
   * exports the given java type as a GraphQL scalar.
   */

  public ExecutableSchemaBuilder addScalar(Type klass) {
    registerType(TypeToken.of(klass), null, LogicalTypeKind.SCALAR);
    return this;
  }

  public ExecutableSchemaBuilder addScalar(TypeToken<?> typeToken, String typeName) {
    registerType(typeToken, typeName, LogicalTypeKind.SCALAR);
    return this;
  }

  /**
   * exports the given java type as a GraphQL scalar.
   */

  public ExecutableSchemaBuilder addScalar(Type klass, String typeName) {
    registerType(TypeToken.of(klass), typeName, LogicalTypeKind.SCALAR);
    return this;
  }

  /**
   * validates the schema.
   */

  public void validate() {

    // build complete types.

    this.names.forEach((typeName, symbol) -> {
      if (symbol.handle == null)
        return;
      symbol.handle
          .outputFields()
          .forEach(this::validate);
    });

  }

  /**
   * validate this type token has an external name.
   */

  private void validate(TypeToken<?> type) {

    if (type.getType().equals(Object.class)) {
      return;
    }

    Preconditions.checkArgument(
        this.types.containsKey(type),
        "type does not have a name: [%s]",
        type.toString());

  }

  /**
   * validate an output field be checking it's return type and parameters.
   */

  private void validate(JavaOutputField field) {
    try {
      validate(field.returnType());
      field.inputFields().forEach(this::validate);
    }
    catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("in field " + field, ex);
    }
  }

  /**
   * validate an input field.
   */

  private void validate(JavaInputField field) {
    try {
      validate(field.inputType());
    }
    catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("in parameter " + field, ex);
    }
  }

  /**
   * validates the schema and returns and immutable instance of it.
   * 
   * throws an exception if the schema has any errors and strict is set.
   * 
   * @return
   */

  public ExecutableSchema build() {
    validate();
    return new ExecutableSchema(this);
  }

  Map<GQLOperationType, Symbol> operationRoots() {
    return this.operationRoots;
  }

  Stream<Symbol> symbols() {
    return this.names.values().stream();
  }

  public void registerExtension(Class<?> klass) {
    this.binder.registerExtension(klass);
  }

  public ExecutableSchemaBuilder extensionGenerator(ExtensionGenerator gen) {
    this.binder.extensionGenerator(gen);
    return this;
  }

}
