package io.zrz.graphql.zulu.executable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
import io.zrz.graphql.zulu.annotations.GQLEnumType;
import io.zrz.graphql.zulu.annotations.GQLInterfaceType;
import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.annotations.GQLScalarType;
import io.zrz.graphql.zulu.annotations.GQLType.Kind;
import io.zrz.graphql.zulu.annotations.GQLUnionType;
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
  private final JavaBindingProvider binder = new JavaBindingProvider();

  /**
   * the registered operation roots.
   */

  private final Map<GQLOperationType, Symbol> operationRoots = new HashMap<>();

  /**
   * the predicate which tests if a token is allowed to be loaded.
   */

  private Predicate<TypeToken<?>> allowedAutoload = tok -> false;

  /**
   *
   */

  private final Map<String, Symbol> names = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  private final Map<TypeToken<?>, Symbol> types = new HashMap<>();
  private final Multimap<LogicalTypeKind, Symbol> kinds = HashMultimap.create();

  public ExecutableSchemaBuilder() {

    this.addBuiltin(String.class, "String", LogicalTypeKind.SCALAR);

    this.addBuiltin(Boolean.class, "Boolean", LogicalTypeKind.SCALAR);
    this.addBuiltin(Integer.class, "Int", LogicalTypeKind.SCALAR);
    this.addBuiltin(Long.class, "Int", LogicalTypeKind.SCALAR);
    this.addBuiltin(Double.class, "Double", LogicalTypeKind.SCALAR);

    this.addBuiltin(Boolean.TYPE, "Boolean", LogicalTypeKind.SCALAR);
    this.addBuiltin(Integer.TYPE, "Int", LogicalTypeKind.SCALAR);
    this.addBuiltin(Long.TYPE, "Int", LogicalTypeKind.SCALAR);
    this.addBuiltin(Double.TYPE, "Double", LogicalTypeKind.SCALAR);

    this.addBuiltin(GQLTypeKind.class, "__TypeKind", LogicalTypeKind.ENUM);
    this.addBuiltin(GQLDirectiveLocation.class, "__DirectiveLocation", LogicalTypeKind.ENUM);

  }

  public ExecutableSchemaBuilder allowedAutoloader(final Predicate<TypeToken<?>> allowed) {
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
    boolean builtin;
    boolean stub;

    public String typeName() {
      return this.typeName;
    }

  }

  /**
   * exposes the query root.
   *
   * @return
   */

  public ExecutableSchemaBuilder setRootType(final GQLOperationType operation, final Type klass) {
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

  public ExecutableSchemaBuilder setRootType(final GQLOperationType operationType, final Type klass, final String typeName) {
    final TypeToken<?> typeToken = TypeToken.of(klass);
    Preconditions.checkState(!this.operationRoots.containsKey(operationType));
    final Symbol symbol = this.registerType(typeToken, typeName, LogicalTypeKind.OUTPUT);
    symbol.exported = true;
    this.operationRoots.put(operationType, symbol);
    return this;
  }

  private void checkSymbol(final TypeToken<?> typeToken, final String typeName, final LogicalTypeKind typeKind) {

    final Symbol symbol = this.names.get(typeName);

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

    final Symbol typeSymbol = this.types.get(typeToken);

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

  private Symbol addSymbol(final TypeToken<?> typeToken, final String typeName, final LogicalTypeKind typeKind, final JavaBindingType handle) {

    final Symbol symbol = new Symbol();

    symbol.typeName = typeName;
    symbol.typeKind = typeKind;
    symbol.typeToken = typeToken;
    symbol.handle = handle;
    symbol.exported = true;

    if (handle == null) {
      symbol.handle = this.binder.include(typeToken);
    }

    switch (symbol.typeName) {
      case "Int":
      case "String":
      case "Boolean":
      case "Double":
        break;
      default:
        Preconditions.checkState(!this.names.containsKey(symbol.typeName), symbol.typeName);
        break;

    }

    this.names.put(symbol.typeName, symbol);
    this.types.put(symbol.typeToken, symbol);
    this.kinds.put(symbol.typeKind, symbol);

    this.scanAddedSymbol(symbol);

    return symbol;

  }

  private Symbol registerType(final TypeToken<?> typeToken, String typeName, final LogicalTypeKind typeKind) {

    if (typeKind.equals(LogicalTypeKind.OUTPUT) || typeKind.equals(LogicalTypeKind.INTERFACE)) {

      final JavaBindingType handle = this.binder.registerType(typeToken);

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

  public ExecutableSchemaBuilder addType(final Type klass) {
    return this.addType(TypeToken.of(klass));
  }

  public ExecutableSchemaBuilder addType(final TypeToken<?> typeToken) {

    final Optional<Kind> kind = JavaExecutableUtils.getType(typeToken);

    if (!kind.isPresent()) {
      throw new IllegalArgumentException("couldn't calculate type kind for " + typeToken.toString() + " - specify a @GQL*Type annotation on the type");
    }

    final LogicalTypeKind typeKind = LogicalTypeKind.from(kind.get());

    final Symbol sym = this.registerType(typeToken, null, typeKind);

    this.autoscan(typeToken);

    return this;
  }

  /**
   * register a stub type - a java type which isn't itself a GQL java type but extensions will provide the functionality
   * for it.
   *
   * @param klass
   * @param typeName
   * @param handle
   * @return
   */

  public ExecutableSchemaBuilder addStubType(final Type klass, String typeName, final JavaBindingType handle) {

    final TypeToken<?> typeToken = TypeToken.of(klass);

    if (typeName == null) {
      typeName = this.generateName(typeToken, handle);
    }

    final Symbol sym = this.addSymbol(typeToken, typeName, LogicalTypeKind.OUTPUT, null);

    sym.stub = true;

    return this;

  }

  /**
   * exposes a java class as a GraphQL type.
   */

  public ExecutableSchemaBuilder addType(final Type klass, final String typeName) {
    this.registerType(TypeToken.of(klass), typeName, LogicalTypeKind.OUTPUT);
    return this;
  }

  /**
   * exports the given java type as a GraphQL interface.
   */

  public ExecutableSchemaBuilder addInterface(final Type klass) {
    final Symbol sym = this.registerType(TypeToken.of(klass), null, LogicalTypeKind.INTERFACE);
    return this;
  }

  /**
   * exports the given java type as a GraphQL union.
   */

  public ExecutableSchemaBuilder addUnion(final Type klass) {
    this.registerType(TypeToken.of(klass), null, LogicalTypeKind.UNION);
    return this;
  }

  /**
   * exports the given java enum as a GraphQL enum
   */

  public ExecutableSchemaBuilder addEnum(final Type klass) {
    this.registerType(TypeToken.of(klass), null, LogicalTypeKind.ENUM);
    return this;
  }

  /**
   * exports the given java enum as a GraphQL enum
   */

  public ExecutableSchemaBuilder addEnum(final Type klass, final String typeName) {
    this.registerType(TypeToken.of(klass), typeName, LogicalTypeKind.ENUM);
    return this;
  }

  /**
   * exports the given java type as a GraphQL scalar.
   */

  public ExecutableSchemaBuilder addScalar(final Type klass) {
    this.registerType(TypeToken.of(klass), null, LogicalTypeKind.SCALAR);
    return this;
  }

  public ExecutableSchemaBuilder addScalar(final TypeToken<?> typeToken, final String typeName) {
    this.registerType(typeToken, typeName, LogicalTypeKind.SCALAR);
    return this;
  }

  /**
   * exports the given java type as a GraphQL scalar.
   */

  public ExecutableSchemaBuilder addScalar(final Type klass, final String typeName) {
    this.registerType(TypeToken.of(klass), typeName, LogicalTypeKind.SCALAR);
    return this;
  }

  private void addBuiltin(final Type klass, final String typeName, final LogicalTypeKind kind) {
    this.registerType(TypeToken.of(klass), typeName, kind).builtin = true;
  }

  /**
   * exports the given java type as a GraphQL scalar.
   */

  public ExecutableSchemaBuilder addInput(final Type klass, final String typeName) {
    this.registerType(TypeToken.of(klass), typeName, LogicalTypeKind.INPUT);
    return this;
  }

  /**
   * validates the schema.
   *
   * @return
   */

  public ValidationResult validate() {

    // build complete types.

    final ValidationResult val = new ValidationResult();

    val.validate();

    return val;

  }

  public interface ValidationTypeUse {
  }

  public interface MissingTypeValidationError extends ValidationTypeUse {

    TypeToken<?> unmappedType();

  }

  public static class ReturnTypeUse implements MissingTypeValidationError {

    private final Symbol symbol;
    private final JavaOutputField field;
    private final TypeToken<?> returnType;

    public ReturnTypeUse(final Symbol symbol, final JavaOutputField field, final TypeToken<?> returnType) {
      this.symbol = symbol;
      this.field = field;
      this.returnType = returnType;
    }

    public String usage() {
      return this.field.origin().map(JavaBindingUtils::toString).orElse(this.field.toString());
    }

    @Override
    public String toString() {
      return "return type '" + this.returnType + "' can not be mapped to GraphQL\n  -> " + this.usage();
    }

    @Override
    public TypeToken<?> unmappedType() {
      return this.returnType;
    }

  }

  public static class ParameterTypeUse implements MissingTypeValidationError {

    private final Symbol symbol;
    private final JavaOutputField field;
    private final JavaInputField param;
    private final TypeToken<?> parameterType;

    public ParameterTypeUse(final Symbol symbol, final JavaOutputField field, final JavaInputField param, final TypeToken<?> type) {
      this.symbol = symbol;
      this.field = field;
      this.param = param;
      this.parameterType = type;
    }

    public String usage() {
      return this.field.origin().map(JavaBindingUtils::toString).orElse(this.field.toString());
    }

    @Override
    public String toString() {
      return "parameter type '" + this.parameterType + "' can not be mapped to GraphQL\n  -> " + this.usage();
    }

    @Override
    public TypeToken<?> unmappedType() {
      return this.parameterType;
    }

  }

  public class ValidationResult {

    private final Multimap<TypeToken<?>, ValidationTypeUse> requires = HashMultimap.create();

    void validate() {

      ExecutableSchemaBuilder.this.names.values()
          .stream()
          .filter(s -> s.handle != null)
          .forEach(this::validate);

    }

    private void validate(final Symbol symbol) {
      symbol.handle
          .outputFields(new OutputFieldFilter() {})
          .forEach(field -> this.validate(symbol, field));
    }

    /**
     * validate an output field be checking it's return type and parameters.
     */

    private void validate(final Symbol symbol, final JavaOutputField field) {
      this.validateReturnType(symbol, field, field.returnType());
      field.inputFields().forEach(param -> this.validate(symbol, field, param));
    }

    private void validateReturnType(final Symbol symbol, final JavaOutputField field, final TypeToken<?> returnType) {

      if (returnType.getType().equals(Object.class)) {
        return;
      }

      if (ExecutableSchemaBuilder.this.types.containsKey(returnType)) {
        return;
      }

      // we need to have a name for this type.

      this.requires.put(returnType, new ReturnTypeUse(symbol, field, returnType));

    }

    /**
     * validate an input field.
     */

    private void validate(final Symbol symbol, final JavaOutputField field, final JavaInputField param) {

      final TypeToken<?> type = param.inputType();

      if (type.getType().equals(Object.class)) {
        return;
      }

      if (ExecutableSchemaBuilder.this.types.containsKey(type)) {
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
    return this.build(true);
  }

  public ExecutableSchema build(final boolean strict) {

    final AtomicBoolean changed = new AtomicBoolean(false);

    do {

      changed.set(false);

      if (false) {

        final ValidationResult val = this.validate();

        if (!val.requires.isEmpty()) {

          if (strict) {
            val.requires.values().forEach(err -> log.error("validation error: {}", err.toString()));
            throw new IllegalArgumentException("validation failed");
          }

          final Set<TypeToken<?>> tried = new HashSet<>();

          val.requires.values().forEach(err -> {

            // see if we can work out what the type is based on it's usage.

            if (err instanceof MissingTypeValidationError) {

              final MissingTypeValidationError m = (MissingTypeValidationError) err;

              final TypeToken<?> type = m.unmappedType();

              if (tried.add(type)) {

                if (this.shouldAutoMap(type)) {
                  changed.set(true);
                  final JavaBindingType handle = this.binder.registerType(type);
                  final Symbol symbol = this.addSymbol(type, this.generateName(type, handle), LogicalTypeKind.OUTPUT, handle);
                  symbol.exported = false;
                }

              }

            }

          });

        }

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

  private boolean shouldAutoMap(final TypeToken<?> type) {

    if (type.isArray()) {
      return false;
    }
    else if (type.getRawType().equals(Void.TYPE)) {
      return false;
    }

    if (JavaExecutableUtils.getType(type).isPresent()) {
      log.debug("auto registering {}", type);
      return true;
    }

    return this.allowedAutoload.test(type);

  }

  /**
   *
   * @param req
   * @param handle
   * @return
   */

  private String generateName(final TypeToken<?> req, final JavaBindingType handle) {

    if (handle != null) {

      for (final GQLObjectType a : handle.analysis().annotations(GQLObjectType.class)) {
        if (!StringUtils.isEmpty(a.name())) {
          return a.name();
        }
      }

      for (final GQLInterfaceType a : handle.analysis().annotations(GQLInterfaceType.class)) {
        if (!StringUtils.isEmpty(a.name())) {
          return a.name();
        }
      }

      for (final GQLEnumType a : handle.analysis().annotations(GQLEnumType.class)) {
        if (!StringUtils.isEmpty(a.name())) {
          return a.name();
        }
      }

      for (final GQLScalarType a : handle.analysis().annotations(GQLScalarType.class)) {
        if (!StringUtils.isEmpty(a.name())) {
          return a.name();
        }
      }

      for (final GQLUnionType a : handle.analysis().annotations(GQLUnionType.class)) {
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

  public void registerExtension(final Class<?> klass) {
    this.binder.registerExtension(klass);
  }

  public ExecutableSchemaBuilder extensionGenerator(final ExtensionGenerator gen) {
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

  JavaOutputMapper mapReturnType(final ExecutableOutputField field, final TypeToken<?> returnType) {
    return new JavaOutputMapper(field, returnType).unwrap();
  }

  JavaInputMapper mapInputType(final ExecutableInputField field) {
    return new JavaInputMapper(field).unwrap();
  }

  /**
   * a chance to autoload the javatype which isn't currently registered as a symbol before failing the method.
   *
   * @param javaType
   * @param user
   * @return
   */

  Symbol autoload(final TypeToken<?> javaType, final ExecutableElement user) {

    // see if any annotations with @GQLType are present

    final Optional<Kind> kind = JavaExecutableUtils.getType(javaType);

    if (!kind.isPresent()) {
      return null;
    }

    if (this.types.containsKey(javaType)) {
      // already have this type.
      return this.types.get(javaType);
    }

    final JavaBindingType handle = this.binder.registerType(javaType);

    final String typeName = this.generateName(javaType, handle);

    log.debug("autoloading {} type {} = {} (used by {})", kind.get(), typeName, javaType, user);

    final Symbol sym = this.addSymbol(
        javaType,
        typeName,
        LogicalTypeKind.from(kind.get()),
        handle);

    return sym;

  }

  void scanAddedSymbol(final Symbol sym) {

    this.autoscan(sym.typeToken);

    // check the parents to find any concrete typed declared in the annotation to avoid
    // having to manually register them or scan packages.

    if (sym.typeKind == LogicalTypeKind.INTERFACE) {
      for (final GQLInterfaceType iface : sym.handle.analysis().annotations(GQLInterfaceType.class)) {
        for (final Class<?> impl : iface.implementations()) {
          if (!this.types.containsKey(TypeToken.of(impl))) {
            this.addType(impl);
          }
        }
      }
    }

  }

  public Stream<? extends JavaOutputField> outputFieldsFor(final Symbol symbol, final ExecutableOutputType type) {
    return this.binder.extensionsFor(symbol.typeToken);
  }

  public Stream<? extends JavaOutputField> outputFieldsFor(final Symbol symbol, final ExecutableInterfaceType type) {
    return this.binder.extensionsFor(symbol.typeToken);
  }

  /**
   * provides a stream of symbols which are declared but not in the provided set.
   *
   * used to calculate types which need to be registered even though they're not referenced directly.
   *
   * @param set
   * @return
   */

  Stream<Symbol> additionalTypes(final Set<Symbol> set) {

    return this.symbols()
        .filter(a -> !set.contains(a))
        .filter(a -> !a.builtin)
        .filter(a -> a.typeKind == LogicalTypeKind.OUTPUT || a.typeKind == LogicalTypeKind.INTERFACE);

  }

  public Set<Symbol> interfacesFor(final Symbol symbol) {
    return this.supertypes(symbol.handle);
  }

  private Set<Symbol> supertypes(final JavaBindingType handle) {

    final Symbol symbol = this.types.get(handle.analysis().javaType());

    if (symbol == null) {
      throw new IllegalArgumentException("failed to find symbol for " + handle.analysis().javaType());
    }

    final TypeToken<?>.TypeSet stypes = symbol.typeToken.getTypes();

    return stypes.stream()
        .filter(t -> !t.equals(symbol.typeToken))
        .map(x -> this.types.get(x))
        .filter(x -> x != null)
        .filter(sym -> sym.typeKind == LogicalTypeKind.INTERFACE)
        .collect(Collectors.toSet());

  }

  /**
   * hint to autoregister other types associasted with this.
   *
   * @param typeToken
   * @return
   */

  private void autoscan(final TypeToken<?> typeToken) {

    final TypeToken<?>.TypeSet stypes = typeToken.getTypes();

    stypes.stream()
        .filter(t -> !t.equals(typeToken))
        .filter(x -> !this.types.containsKey(x))
        .filter(e -> this.shouldAutoMap(e))
        .forEach(type -> this.addType(type));

  }
}
