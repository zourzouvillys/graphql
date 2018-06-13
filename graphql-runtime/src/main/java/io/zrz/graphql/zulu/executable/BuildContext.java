package io.zrz.graphql.zulu.executable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;

class BuildContext {

  Map<Symbol, ExecutableType> types = new HashMap<>();
  private ExecutableSchemaBuilder b;
  private Set<Symbol> target;
  private Map<TypeToken<?>, Symbol> suppliers;
  private ExecutableSchema schema;

  BuildContext(ExecutableSchemaBuilder b, ExecutableSchema schema) {
    this.schema = schema;
    this.b = b;
    this.suppliers = b.symbols().collect(Collectors.toMap(s -> s.typeToken, s -> s));
    this.target = b.symbols().collect(Collectors.toSet());
  }

  ExecutableTypeUse use(ExecutableElement user, TypeToken<?> javaType) {

    Symbol symbol = this.suppliers.get(javaType);

    Preconditions.checkState(symbol != null, "couldn't find a symbol for '%s'", javaType);

    ExecutableType found = types.get(symbol);

    if (found == null) {

      switch (symbol.typeKind) {
        case OUTPUT:
          types.put(symbol, new ExecutableOutputType(schema, symbol, this));
          return new ExecutableTypeUse(javaType, symbol.typeName);
        case ENUM:
        case INPUT:
        case INTERFACE:
        case SCALAR:
        case UNION:
          this.compile(symbol);
          return new ExecutableTypeUse(javaType, symbol.typeName);
        default:
      }

      throw new IllegalArgumentException(symbol.typeKind.toString());

    }

    return new ExecutableTypeUse(javaType, found);

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
      case ENUM:
      case INPUT:
      case INTERFACE:
      case UNION:
      default:
    }

    throw new IllegalArgumentException(symbol.typeKind.toString());

  }

}
