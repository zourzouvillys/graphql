package io.zrz.zulu.values;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZStructType;

public class ZStructValueBuilder implements ZStructValue {

  private ZStructType type;
  private HashMap<String, ZValue> values = new HashMap<>();

  public ZStructValueBuilder(ZStructType struct) {

    this.type = struct;

    type.fields().entrySet().stream().filter(x -> x.getValue().constantValue().isPresent())
        .forEach(e -> values.put(e.getKey(), e.getValue().constantValue().get()));

  }

  public ZStructValue build() {

    type.fields().entrySet().stream()
        .filter(e -> !this.values.containsKey(e.getKey()))
        .filter(x -> x.getValue().defaultValue().isPresent())
        .forEach(e -> values.put(e.getKey(), e.getValue().defaultValue().get()));

    Set<Entry<String, ? extends ZField>> missing = type.fields().entrySet().stream()
        .filter(e -> !this.values.containsKey(e.getKey()))
        .collect(Collectors.toSet());

    if (!missing.isEmpty()) {
      throw new IllegalArgumentException("struct missing fields: " + missing.stream().map(x -> x.getKey()).collect(Collectors.joining(", ", "[ ", " ]")));
    }

    return this;
  }

  public ZStructValueBuilder put(String fieldName, Optional<ZValue> value) {
    value.ifPresent(val -> values.put(fieldName, val));
    return this;
  }

  @Override
  public ZStructType valueType() {
    return this.type;
  }

  @Override
  public Optional<ZValue> fieldValue(String fieldName) {
    return Optional.ofNullable(values.get(fieldName));
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append("struct {\n");

    this.values.entrySet().forEach(e -> {

      sb.append("  ");
      sb.append(e.getKey());
      sb.append(" = ");
      sb.append(e.getValue());
      sb.append("\n");

    });

    sb.append("}\n");

    return sb.toString();

  }

  public ZStructValueBuilder put(String fieldName, ZValue fieldValue) {
    if (fieldValue == null) {
      throw new IllegalArgumentException("fieldValue");
    }
    this.put(fieldName, Optional.of(fieldValue));
    return this;
  }

}
