package io.zrz.graphql.zulu.doc;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLValueConverters;
import io.zrz.graphql.core.value.GQLValueType;
import io.zrz.zulu.types.ZArrayType;
import io.zrz.zulu.types.ZPrimitiveArrayTypes;
import io.zrz.zulu.values.ZArrayValue;
import io.zrz.zulu.values.ZValue;
import io.zrz.zulu.values.ZValues;

public class LocalZValues {

  public static ZArrayValue ofList(final String... values) {

    return new ZArrayValue() {

      @Override
      public Stream<ZValue> values() {
        return Stream.of(values).map(value -> ZValues.of(value));
      }

      @Override
      public ZArrayType valueType() {
        return ZPrimitiveArrayTypes.STRING_ARRAY;
      }

      @Override
      public int size() {
        throw new IllegalArgumentException();
      }

      @Override
      public ZValue get(final int index) {
        throw new IllegalArgumentException();
      }

    };

  }

  public static Optional<ZValue> toList(final GQLListValue value) {
    // find the type of the list ...

    final Set<GQLValueType> types = value.values().stream().map(val -> val.type())
        .distinct()
        .collect(Collectors.toSet());

    if (types.size() != 1) {
      throw new IllegalArgumentException("all types in a list must be the same");
    }

    final GQLValueType type = types.iterator().next();

    switch (type) {
      case String:
      case Enum:
        return Optional.of(LocalZValues.ofList(value.values().stream().map(x -> x.apply(GQLValueConverters.stringConverter())).toArray(String[]::new)));
      case Boolean:
      case Float:
      case Int:
      case List:
      case Object:
      case VariableRef:
      default:
        throw new IllegalArgumentException(type.name());
    }

  }
}
