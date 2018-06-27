package io.zrz.graphql.zulu.executable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;
import io.zrz.graphql.zulu.binding.JavaBindingMethodAnalysis;
import io.zrz.graphql.zulu.binding.OutputFieldFilter;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;

class BuildContext implements OutputFieldFilter {

  Map<Symbol, ExecutableType> types = new HashMap<>();
  private final ExecutableSchemaBuilder b;
  private final Set<Symbol> target;
  Map<TypeToken<?>, Symbol> suppliers;
  private final ExecutableSchema schema;

  BuildContext(final ExecutableSchemaBuilder b, final ExecutableSchema schema) {
    this.schema = schema;
    this.b = b;
    this.suppliers = b.symbols().collect(Collectors.toMap(s -> s.typeToken, s -> s));
    this.target = b.symbols().collect(Collectors.toSet());
  }

  ExecutableSchemaBuilder builder() {
    return this.b;
  }

  ExecutableTypeUse use(final ExecutableElement user, final TypeToken<?> javaType, final boolean nullable) {
    return this.use(user, javaType, 0, nullable, new Annotation[0]);
  }

  ExecutableTypeUse use(final ExecutableElement user, final TypeToken<?> javaType, final int arity, final boolean nullable) {
    return this.use(user, javaType, arity, nullable, new Annotation[0]);
  }

  ExecutableTypeUse use(final ExecutableElement user, final TypeToken<?> javaType, final int arity, boolean nullable, final Annotation[] typeuseants) {

    Symbol symbol = null;

    // overriden annotation?
    if (typeuseants.length > 0) {

      for (final Annotation ant : typeuseants) {

        if (ant.annotationType().equals(GQLTypeUse.class)) {

          final GQLTypeUse tu = (GQLTypeUse) ant;

          nullable = tu.nullable();

          final String typeName = tu.name();

          if (StringUtils.isEmpty(typeName)) {
            continue;
          }

          symbol = this.types
              .keySet()
              .stream()
              .filter(msym -> msym.typeName.equals(typeName))
              .findFirst()
              .orElse(null);

          if (symbol == null) {
            symbol = this.b.resolve(typeName, javaType, tu);
          }

          if (symbol == null) {
            throw new IllegalArgumentException("typename '" + typeName + "' specified in @GQLTypeUse is unknown (used at " + user + ")");
          }

        }

      }

    }

    if (symbol == null) {
      symbol = this.suppliers.get(javaType);
    }

    if (symbol == null) {

      // a chance to autoload before failing it.

      symbol = this.b.autoload(javaType, user);

      if (symbol != null) {
        this.target.add(symbol);
        this.suppliers.put(javaType, symbol);
      }

    }

    Preconditions.checkState(
        symbol != null,
        "couldn't find a symbol for %s '%s' for %s",
        javaType.getType().getClass().getSimpleName(),
        javaType,
        user);

    ExecutableType found = this.types.get(symbol);

    if (found == null) {

      try {

        switch (symbol.typeKind) {
          case OUTPUT:
          case ENUM:
          case INPUT:
          case INTERFACE:
          case SCALAR:
          case UNION: {

            found = this.compile(symbol);

            // this.types.put(symbol, decl);
            Preconditions.checkArgument(this.types.containsKey(symbol));
            Preconditions.checkArgument(this.types.get(symbol) == found);

            break;

          }
          default:

            throw new IllegalArgumentException(symbol.typeKind.toString());

        }

      }
      catch (final Exception ex) {

        throw new RuntimeException("error building '" + symbol.typeName + "' from " + symbol.typeToken, ex);

      }

    }

    final ExecutableTypeUse t = new ExecutableTypeUse(javaType, symbol.typeName, arity, symbol, found, nullable);

    return t;

  }

  Set<Symbol> pending() {
    return this.target
        .stream()
        .filter(s -> s.typeKind == LogicalTypeKind.OUTPUT)
        .filter(s -> !this.types.containsKey(s))
        .collect(Collectors.toSet());
  }

  void add(final Symbol symbol, final ExecutableType type) {

    final ExecutableType existing = this.types.get(symbol);

    if (existing != null) {
      if (existing != type) {
        throw new IllegalArgumentException("attempted to register symbol " + symbol.typeName + " multiple times");
      }
      return;
    }

    this.types.put(symbol, type);

    this.target.remove(symbol);

    if (this.suppliers.containsKey(symbol.typeToken)) {
      // autoregistered, didn't know about type on start
      this.suppliers.put(symbol.typeToken, symbol);
    }

  }

  public ExecutableType compile(final Symbol symbol) {

    final ExecutableType found = this.types.get(symbol);

    if (found != null) {
      return found;
    }

    switch (symbol.typeKind) {
      case OUTPUT: {
        final ExecutableOutputType value = new ExecutableOutputType(this.schema, symbol, this);
        Preconditions.checkArgument(this.types.containsKey(symbol));
        Preconditions.checkArgument(this.types.get(symbol) == value);
        return value;
      }
      case SCALAR: {
        final ExecutableScalarType value = new ExecutableScalarType(this.schema, symbol, this);
        Preconditions.checkArgument(this.types.containsKey(symbol));
        Preconditions.checkArgument(this.types.get(symbol) == value);
        return value;
      }
      case INPUT: {
        final ExecutableInputType value = new ExecutableInputType(this.schema, symbol, this);
        Preconditions.checkArgument(this.types.containsKey(symbol));
        Preconditions.checkArgument(this.types.get(symbol) == value);
        return value;
      }
      case ENUM: {
        final ExecutableEnumType value = new ExecutableEnumType(this.schema, symbol, this);
        Preconditions.checkArgument(this.types.containsKey(symbol));
        Preconditions.checkArgument(this.types.get(symbol) == value);
        return value;
      }
      case INTERFACE: {
        final ExecutableInterfaceType value = new ExecutableInterfaceType(this.schema, symbol, this);
        Preconditions.checkArgument(this.types.containsKey(symbol));
        Preconditions.checkArgument(this.types.get(symbol) == value);
        return value;
      }
      case UNION:
      default:
    }

    throw new IllegalArgumentException(symbol.typeKind.toString());

  }

  /**
   * returns the filter to use for scanning the executabletype.
   *
   * @param type
   * @return
   */

  public OutputFieldFilter filterFor(final ExecutableOutputType type) {
    return this;
  }

  public OutputFieldFilter filterFor(final ExecutableInterfaceType type) {
    return this;
  }

  /**
   * true if the given method should be included as a field in the generation.
   */

  @Override
  public boolean shouldInclude(final JavaBindingMethodAnalysis m) {

    if (m.origin().isPresent()) {

      final Method method = m.origin().get();

      final Method declaring = JavaExecutableUtils.getDeclaredMethod(method);

      if (declaring.getDeclaringClass().equals(Object.class)) {
        return false;
      }
      else if (declaring.getDeclaringClass().equals(Comparable.class)) {
        return false;
      }

      if (method.getDeclaringClass().equals(declaring.getDeclaringClass())) {
        return true;
      }

      // otherwise, only accept the most precise

      return declaring.getDeclaringClass().isAssignableFrom(method.getDeclaringClass());

    }

    return true;
  }

  /**
   * provides the fields which the given output type will have.
   *
   * @param symbol
   * @param type
   * @return
   */

  public Stream<? extends JavaOutputField> outputFieldsFor(final Symbol symbol, final ExecutableOutputType type) {

    if (symbol.handle == null || symbol.stub) {
      return this.builder().outputFieldsFor(symbol, type);
    }

    return Stream.concat(
        this.builder().outputFieldsFor(symbol, type),
        symbol.handle.outputFields(this.filterFor(type)));

  }

  public Stream<? extends JavaOutputField> outputFieldsFor(final Symbol symbol, final ExecutableInterfaceType type) {
    if (symbol.handle == null) {
      return this.builder().outputFieldsFor(symbol, type);
    }
    return Stream.concat(
        this.builder().outputFieldsFor(symbol, type),
        symbol.handle.outputFields(this.filterFor(type)));

  }

  /**
   * assign the interfaces for the specified type.
   *
   * the search strategy uses the java type hierachy, and annotations.
   *
   * @param symbol
   * @param objectType
   *
   * @return
   */

  public Set<ExecutableInterfaceType> interfacesFor(final Symbol symbol, final ExecutableReceiverType receiverType) {

    Objects.requireNonNull(symbol.handle, symbol.typeName);

    return this.b.interfacesFor(symbol)
        .stream()
        .map(ifacesym -> this.compile(ifacesym))
        .map(type -> (ExecutableInterfaceType) type)
        .collect(Collectors.toSet());

  }

}
