package io.zrz.graphql.zulu.executable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public class ExecutableInterfaceType implements ExecutableType, ExecutableElement, ExecutableStructType, ExecutableReceiverType {

  private final String typeName;
  private final ImmutableMap<String, ExecutableOutputField> fields;
  private final Set<ExecutableInterfaceType> interfaces;
  private final TypeToken<?> javaType;

  public ExecutableInterfaceType(final ExecutableSchema schema, final Symbol symbol, final BuildContext buildctx) {

    buildctx.add(symbol, this);

    this.javaType = symbol.typeToken;
    this.typeName = symbol.typeName;

    final Map<String, ExecutableOutputField> declaredFields = buildctx
        .outputFieldsFor(symbol, this).map(field -> new ExecutableOutputField(this, symbol, field, buildctx))
        .collect(ImmutableMap.toImmutableMap(k -> k.fieldName(), k -> k, this::mergeFields));

    this.interfaces = buildctx.interfacesFor(symbol, this);

    this.fields = ImmutableMap.copyOf(declaredFields);

  }

  private ExecutableOutputField mergeFields(final ExecutableOutputField a, final ExecutableOutputField b) {
    return JavaExecutableUtils.merge(this, a, b);
  }

  @Override
  public ZTypeKind typeKind() {
    return ZTypeKind.STRUCT;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.INTERFACE;
  }

  @Override
  public String typeName() {
    return this.typeName;
  }

  @Override
  public Map<String, ExecutableOutputField> fields() {
    return Objects.requireNonNull(this.fields, "fields for '" + this.typeName + "' not yet loaded");
  }

  @Override
  public Optional<ExecutableOutputField> field(final String fieldName) {
    return Optional.ofNullable(this.fields.get(fieldName));
  }

  @Override
  public TypeToken<?> javaType() {
    return this.javaType;
  }

}
