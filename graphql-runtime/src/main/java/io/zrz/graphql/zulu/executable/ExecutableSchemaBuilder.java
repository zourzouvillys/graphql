package io.zrz.graphql.zulu.executable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.core.decl.GQLDirectiveLocation;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.core.types.GQLTypeKind;
import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.annotations.GQLOutputType;
import io.zrz.graphql.zulu.binding.JavaBindingProvider;
import io.zrz.graphql.zulu.binding.JavaBindingType;
import io.zrz.graphql.zulu.binding.JavaBindingUtils;
import io.zrz.graphql.zulu.binding.OutputFieldFilter;
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

  private static Logger log = LoggerFactory.getLogger(ExecutableSchemaBuilder.class);

  // the binder component is stateful, as it knows types previously registered.
  private JavaBindingProvider binder = new JavaBindingProvider();

  /**
   * the registered operation roots.
   */

  private Map<GQLOperationType, Symbol> operationRoots = new HashMap<>();

  /**
   * the predicate which tests if a token is allowed to be loaded.
   */

  private Predicate<TypeToken<?>> allowedAutoload = tok -> false;

  /**
   * 
   */

  private Map<String, Symbol> names = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  private Map<TypeToken<?>, Symbol> types = new HashMap<>();
  private Multimap<LogicalTypeKind, Symbol> kinds = HashMultimap.create();

  public ExecutableSchemaBuilder() {

    this.addScalar(String.class, "String");

    this.addScalar(Boolean.class, "Boolean");
    this.addScalar(Integer.class, "Int");
    this.addScalar(Long.class, "Int");
    this.addScalar(Double.class, "Double");

    this.addScalar(Boolean.TYPE, "Boolean");
    this.addScalar(Integer.TYPE, "Int");
    this.addScalar(Long.TYPE, "Int");
    this.addScalar(Double.TYPE, "Double");

    this.addEnum(GQLTypeKind.class, "__TypeKind");
    this.addEnum(GQLDirectiveLocation.class, "__DirectiveLocation");

  }

  public ExecutableSchemaBuilder allowedAutoloader(Predicate<TypeToken<?>> allowed) {
    this.allowedAutoload = allowed;
    return this;
  }

  /**
   * 
   */

  static class Symbol {
    String typeName;
    LogicalTypeKind typeKind;
    TypeToken<?> typeToken;
    JavaBindingType handle;
    // if we received explicit indication of this symbol being exported, e.g it was specified manually or through a
    // scanner.
    boolean exported;
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
    symbol.exported = true;
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

      if (!typeToken.equals(symbol.typeToken) && !typeToken.isPrimitive() && symbol.typeKind != LogicalTypeKind.SCALAR) {
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

    if (handle == null && typeKind == LogicalTypeKind.SCALAR) {

      Symbol symbol = this.types.get(typeToken);

      if (symbol != null) {
        // it's an additional name.
      }

    }

    Symbol symbol = new Symbol();

    symbol.typeName = typeName;
    symbol.typeKind = typeKind;
    symbol.typeToken = typeToken;
    symbol.handle = handle;
    symbol.exported = true;

    this.names.put(symbol.typeName, symbol);
    this.types.put(symbol.typeToken, symbol);
    this.kinds.put(symbol.typeKind, symbol);

    return symbol;

  }

  private Symbol registerType(TypeToken<?> typeToken, String typeName, LogicalTypeKind typeKind) {
    if (typeKind.equals(LogicalTypeKind.OUTPUT)) {

      JavaBindingType handle = binder.registerType(typeToken);

      if (typeName == null) {
        typeName = this.generateName(typeToken, handle);
      }

      return this.addSymbol(typeToken, typeName, typeKind, handle);

    }
    else {
      if (typeName == null) {
        typeName = this.generateName(typeToken, null);
      }
      this.checkSymbol(typeToken, typeName, typeKind);
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
   * exports the given java enum as a GraphQL enum
   */

  public ExecutableSchemaBuilder addEnum(Type klass, String typeName) {
    registerType(TypeToken.of(klass), typeName, LogicalTypeKind.ENUM);
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
   * exports the given java type as a GraphQL scalar.
   */

  public ExecutableSchemaBuilder addInput(Type klass, String typeName) {
    registerType(TypeToken.of(klass), typeName, LogicalTypeKind.INPUT);
    return this;
  }

  /**
   * validates the schema.
   * 
   * @return
   */

  public ValidationResult validate() {

    // build complete types.

    ValidationResult val = new ValidationResult();

    val.validate();

    return val;

  }

  public interface ValidationTypeUse {
  }

  public interface MissingTypeValidationError extends ValidationTypeUse {

    TypeToken<?> unmappedType();

  }

  public static class ReturnTypeUse implements MissingTypeValidationError {

    private Symbol symbol;
    private JavaOutputField field;
    private TypeToken<?> returnType;

    public ReturnTypeUse(Symbol symbol, JavaOutputField field, TypeToken<?> returnType) {
      this.symbol = symbol;
      this.field = field;
      this.returnType = returnType;
    }

    public String usage() {
      return field.origin().map(JavaBindingUtils::toString).orElse(field.toString());
    }

    @Override
    public String toString() {
      return "return type '" + returnType + "' can not be mapped to GraphQL\n  -> " + usage();
    }

    @Override
    public TypeToken<?> unmappedType() {
      return returnType;
    }

  }

  public static class ParameterTypeUse implements MissingTypeValidationError {

    private Symbol symbol;
    private JavaOutputField field;
    private JavaInputField param;
    private TypeToken<?> parameterType;

    public ParameterTypeUse(Symbol symbol, JavaOutputField field, JavaInputField param, TypeToken<?> type) {
      this.symbol = symbol;
      this.field = field;
      this.param = param;
      this.parameterType = type;
    }

    public String usage() {
      return field.origin().map(JavaBindingUtils::toString).orElse(field.toString());
    }

    @Override
    public String toString() {
      return "parameter type '" + parameterType + "' can not be mapped to GraphQL\n  -> " + usage();
    }

    @Override
    public TypeToken<?> unmappedType() {
      return parameterType;
    }

  }

  public class ValidationResult {

    private Multimap<TypeToken<?>, ValidationTypeUse> requires = HashMultimap.create();

    void validate() {

      names.values()
          .stream()
          .filter(s -> s.handle != null)
          .forEach(this::validate);

    }

    private void validate(Symbol symbol) {
      symbol.handle
          .outputFields(new OutputFieldFilter() {})
          .forEach(field -> validate(symbol, field));
    }

    /**
     * validate an output field be checking it's return type and parameters.
     */

    private void validate(Symbol symbol, JavaOutputField field) {
      validateReturnType(symbol, field, field.returnType());
      field.inputFields().forEach(param -> validate(symbol, field, param));
    }

    private void validateReturnType(Symbol symbol, JavaOutputField field, TypeToken<?> returnType) {

      if (returnType.getType().equals(Object.class)) {
        return;
      }

      if (types.containsKey(returnType)) {
        return;
      }

      // we need to have a name for this type.

      this.requires.put(returnType, new ReturnTypeUse(symbol, field, returnType));

    }

    /**
     * validate an input field.
     */

    private void validate(Symbol symbol, JavaOutputField field, JavaInputField param) {

      TypeToken<?> type = param.inputType();

      if (type.getType().equals(Object.class)) {
        return;
      }

      if (types.containsKey(type)) {
        return;
      }

      this.requires.put(type, new ParameterTypeUse(symbol, field, param, type));

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
    return build(true);
  }

  public ExecutableSchema build(boolean strict) {

    AtomicBoolean changed = new AtomicBoolean(false);

    do {

      changed.set(false);

      ValidationResult val = validate();

      if (!val.requires.isEmpty()) {

        if (strict) {
          val.requires.values().forEach(err -> log.error("validation error: {}", err.toString()));
          throw new IllegalArgumentException("validation failed");
        }

        Set<TypeToken<?>> tried = new HashSet<>();

        val.requires.values().forEach(err -> {

          // see if we can work out what the type is based on it's usage.

          if (err instanceof MissingTypeValidationError) {

            MissingTypeValidationError m = (MissingTypeValidationError) err;

            TypeToken<?> type = m.unmappedType();

            if (tried.add(type)) {

              if (shouldAutoMap(type)) {
                changed.set(true);
                JavaBindingType handle = this.binder.registerType(type);
                Symbol symbol = addSymbol(type, generateName(type, handle), LogicalTypeKind.OUTPUT, handle);
                symbol.exported = false;
              }

            }

          }

        });

      }

    }
    while (changed.get());

    return new ExecutableSchema(this);

  }

  /**
   * true if we should take a crack at auto-registering this type.
   * 
   * auto registering only happens if the type was directly exported.
   * 
   * @param type
   * @return
   */

  private boolean shouldAutoMap(TypeToken<?> type) {

    if (type.isArray()) {
      return false;
    }
    else if (type.getRawType().equals(Void.TYPE)) {
      return false;
    }

    if (type.getRawType().isAnnotationPresent(GQLOutputType.class)) {
      log.debug("auto registering {}", type);
      return true;
    }

    return allowedAutoload.test(type);

  }

  /**
   * 
   * @param req
   * @param handle
   * @return
   */

  private String generateName(TypeToken<?> req, JavaBindingType handle) {

    if (handle != null) {
      // TODO: also for enum, scalar, etc ...
      for (GQLOutputType a : handle.analysis().annotations(GQLOutputType.class)) {
        if (!StringUtils.isEmpty(a.name())) {
          return a.name();
        }
      }
    }

    return JavaBindingUtils.autoTypeName(req);
  }

  Map<GQLOperationType, Symbol> operationRoots() {
    return this.operationRoots;
  }

  Stream<Symbol> symbols() {
    // return from here so we get all symbols even when name conflicts.
    return this.types.values().stream();
  }

  public void registerExtension(Class<?> klass) {
    this.binder.registerExtension(klass);
  }

  public ExecutableSchemaBuilder extensionGenerator(ExtensionGenerator gen) {
    this.binder.extensionGenerator(gen);
    return this;
  }

  /**
   * maps the return type to the target GraphQL type that the field maps to.
   * 
   * @param field
   * @param returnType
   * @return
   */

  JavaOutputMapper mapReturnType(ExecutableOutputField field, TypeToken<?> returnType) {
    return new JavaOutputMapper(field, returnType).unwrap();
  }

  JavaInputMapper mapInputType(ExecutableInputField field) {
    return new JavaInputMapper(field).unwrap();
  }

  /**
   * a chance to autoload the javatype which isn't currently registered as a symbol before failing the method.
   * 
   * @param javaType
   * @param user
   * @return
   */

  Symbol autoload(TypeToken<?> javaType, ExecutableElement user) {
    if (!javaType.getRawType().isAnnotationPresent(GQLOutputType.class)) {
      return null;
    }
    JavaBindingType handle = this.binder.registerType(javaType);
    String typeName = this.generateName(javaType, handle);
    log.info("autoloading OUTPUT type {} = {} (used by {})", typeName, javaType, user);
    return this.addSymbol(
        javaType,
        typeName,
        LogicalTypeKind.OUTPUT,
        handle);
  }

}
