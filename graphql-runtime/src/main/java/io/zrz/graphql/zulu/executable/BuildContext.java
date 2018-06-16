package io.zrz.graphql.zulu.executable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.binding.JavaBindingMethodAnalysis;
import io.zrz.graphql.zulu.binding.OutputFieldFilter;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;

class BuildContext implements OutputFieldFilter {

  Map<Symbol, ExecutableType> types = new HashMap<>();
  private ExecutableSchemaBuilder b;
  private Set<Symbol> target;
  Map<TypeToken<?>, Symbol> suppliers;
  private ExecutableSchema schema;

  BuildContext(ExecutableSchemaBuilder b, ExecutableSchema schema) {
    this.schema = schema;
    this.b = b;
    this.suppliers = b.symbols().collect(Collectors.toMap(s -> s.typeToken, s -> s));
    this.target = b.symbols().collect(Collectors.toSet());
  }

  ExecutableSchemaBuilder builder() {
    return this.b;
  }

  ExecutableTypeUse use(ExecutableElement user, TypeToken<?> javaType) {
    return use(user, javaType, 0);
  }

  ExecutableTypeUse use(ExecutableElement user, TypeToken<?> javaType, int arity) {

    Symbol symbol = this.suppliers.get(javaType);

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

    ExecutableType found = types.get(symbol);

    if (found == null) {

      try {

        switch (symbol.typeKind) {
          case OUTPUT: {
            ExecutableOutputType decl = new ExecutableOutputType(schema, symbol, this);
            types.put(symbol, decl);
            return new ExecutableTypeUse(javaType, symbol.typeName, arity, symbol, decl);
          }
          case ENUM:
          case INPUT:
          case INTERFACE:
          case SCALAR:
          case UNION: {
            ExecutableType decl = this.compile(symbol);
            types.put(symbol, decl);
            return new ExecutableTypeUse(javaType, symbol.typeName, arity, symbol, decl);
          }
          default:
            break;
        }

        throw new IllegalArgumentException(symbol.typeKind.toString());

      }
      catch (Exception ex) {

        throw new RuntimeException("error building '" + symbol.typeName + "' from " + symbol.typeToken, ex);

      }

    }

    return new ExecutableTypeUse(javaType, found, arity, symbol);

  }

  Set<Symbol> pending() {
    return target.stream()
        .filter(s -> s.typeKind == LogicalTypeKind.OUTPUT)
        .filter(s -> !types.containsKey(s))
        .collect(Collectors.toSet());
  }

  void add(Symbol symbol, ExecutableType type) {
    types.putIfAbsent(symbol, type);
  }

  public ExecutableType compile(Symbol symbol) {

    ExecutableType found = types.get(symbol);

    if (found != null) {
      return found;
    }

    switch (symbol.typeKind) {
      case OUTPUT: {
        ExecutableOutputType value = new ExecutableOutputType(schema, symbol, this);
        types.put(symbol, value);
        return value;
      }
      case SCALAR: {
        ExecutableScalarType value = new ExecutableScalarType(schema, symbol, this);
        types.put(symbol, value);
        return value;
      }
      case INPUT: {
        ExecutableInputType value = new ExecutableInputType(schema, symbol, this);
        types.put(symbol, value);
        return value;
      }
      case ENUM: {
        ExecutableEnumType value = new ExecutableEnumType(schema, symbol, this);
        types.put(symbol, value);
        return value;
      }
      case INTERFACE:
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

  public OutputFieldFilter filterFor(ExecutableOutputType type) {
    return this;
  }

  /**
   * true if the given method should be included as a field in the generation.
   */

  @Override
  public boolean shouldInclude(JavaBindingMethodAnalysis m) {
    return true;
  }

}
