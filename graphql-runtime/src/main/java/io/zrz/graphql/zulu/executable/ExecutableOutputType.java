package io.zrz.graphql.zulu.executable;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.FuzzyScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.ZOutputType;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public final class ExecutableOutputType implements ExecutableType, ZOutputType, ExecutableElement, ExecutableReceiverType, ExecutableStructType {

  private static Logger log = LoggerFactory.getLogger(ExecutableOutputType.class);

  private final ExecutableSchema schema;
  private final String typeName;
  private final ImmutableMap<String, ExecutableOutputField> fields;
  private final ExecutableTypeUse typeUse;
  private final TypeToken<?> javaType;
  private final String documentation;
  private final Set<ExecutableInterfaceType> interfaces;

  ExecutableOutputType(final ExecutableSchema schema, final Symbol symbol, final BuildContext buildctx) {

    buildctx.add(symbol, this);

    this.schema = schema;
    this.typeName = symbol.typeName;
    this.javaType = symbol.typeToken;

    if (symbol.handle != null) {

      this.documentation = symbol.handle
          .analysis()
          .annotations(GQLDocumentation.class)
          .stream()
          .map(a -> StringUtils.trimToNull(a.value()))
          .filter(text -> text != null)
          .collect(Collectors.joining("\n\n"));

    }
    else {
      this.documentation = null;
    }

    // interfaces are calculated based on the type hierachy.
    this.interfaces = buildctx.interfacesFor(symbol, this);

    // first pass selects best fields for this class.
    final Map<String, JavaOutputField> fields = buildctx
        .outputFieldsFor(symbol, this)
        .sorted(Comparator.comparing(JavaOutputField::fieldName))
        .collect(ImmutableMap.toImmutableMap(JavaOutputField::fieldName, Function.identity(), this::mergeFields));

    final Map<String, ExecutableOutputField> declaredFields = fields
        .values()
        .stream()
        .sorted(Comparator.comparing(JavaOutputField::fieldName))
        .map(field -> this.buildField(field, symbol, buildctx))
        .collect(ImmutableMap.toImmutableMap(ExecutableOutputField::fieldName, Function.identity(), this::mergeFields));

    this.fields = Stream.concat(

        declaredFields
            .values()
            .stream()
            .sorted(Comparator.comparing(ExecutableOutputField::fieldName)),

        this.interfaces
            .stream()
            .flatMap(x -> x.fields().values().stream())
            .filter(f -> !declaredFields.containsKey(f.fieldName()))
            .sorted(Comparator.comparing(ExecutableOutputField::fieldName))

    )
        .collect(
            ImmutableMap.toImmutableMap(
                k -> k.fieldName(),
                k -> k,
                (a, b) -> JavaExecutableUtils.merge(this, a, b)));

    this.typeUse = buildctx.use(this, symbol.typeToken, 0);

  }

  private ExecutableOutputField mergeFields(final ExecutableOutputField a, final ExecutableOutputField b) {
    return JavaExecutableUtils.merge(this, a, b);
  }

  private JavaOutputField mergeFields(final JavaOutputField a, final JavaOutputField b) {
    return JavaExecutableUtils.merge(this, a, b);
  }

  private ExecutableOutputField buildField(final JavaOutputField field, final Symbol symbol, final BuildContext buildctx) {
    try {
      return new ExecutableOutputField(this, symbol, field, buildctx);
    }
    catch (final Throwable ex) {
      throw new RuntimeException("error building field '" + field.fieldName() + "'" + " in '" + symbol.typeName + "'", ex);
    }
  }

  /**
   * schema this type is part of.
   */

  public ExecutableSchema schema() {
    return this.schema;
  }

  /**
   * the interfaces implemented by object type.
   */

  public Set<ExecutableInterfaceType> interfaces() {
    return this.interfaces;
  }

  /**
   * each executable has an app specific context value which represents the specific instance of the type being operated
   * on.
   *
   * for the query root type, this is normally a "viewer" type, which represents the entry point into the model. for
   * type nodes it would normally be something representing that type, e.g an instance of a java type representing it or
   * a database identifier.
   *
   * this value is normally only relevant to the caller for the root types (and is normally the root type itself), as it
   * must pass it in to execute anything.
   *
   * this will always return a logical type of kind {@link io.zrz.graphql.zulu.LogicalTypeKind.LogicalTypeKind#OUTPUT}.
   *
   */

  public ExecutableTypeUse contextType() {
    return this.typeUse;
  }

  /**
   * the java type that this output type represents.
   */

  @Override
  public TypeToken<?> javaType() {
    return this.javaType;
  }

  /**
   * the fields in this output type.
   */

  @Override
  public Map<String, ExecutableOutputField> fields() {
    return this.fields;
  }

  @Override
  public Optional<ExecutableOutputField> field(final String name) {
    return Optional.ofNullable(this.fields().get(name));
  }

  @Override
  public ZTypeKind typeKind() {
    return ZTypeKind.STRUCT;
  }

  @Override
  public String typeName() {
    return this.typeName;
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return LogicalTypeKind.OUTPUT;
  }

  @Override
  public String documentation() {
    return this.documentation;
  }

  /**
   * given a field name, returns any fields that are similar to that name.
   *
   * @param fieldName
   */

  private static FuzzyScore SCORE = new FuzzyScore(Locale.ROOT);

  public List<String> similarFields(final String fieldName, final int count) {
    return this.fieldNames()
        .filter(name -> SCORE.fuzzyScore(fieldName, name) > 0)
        .sorted((a, b) -> Integer.compare(SCORE.fuzzyScore(fieldName, b), SCORE.fuzzyScore(fieldName, b)))
        .limit(count)
        .collect(Collectors.toList());
  }

  public IllegalArgumentException missingFieldException(final String fieldName) {

    final StringBuilder sb = new StringBuilder();

    sb.append("field ");
    sb.append(this.typeName());
    sb.append(".");
    sb.append(fieldName);
    sb.append(" does not exist.");

    final List<String> similar = this.similarFields(fieldName, 5);

    if (!similar.isEmpty()) {

      if (similar.size() == 1) {

        sb.append(" perhaps you meant '");
        sb.append(similar.get(0));
        sb.append("'?");

      }
      else {

        sb.append("perhaps you meant ");

        for (int i = 0; i < similar.size() - 1; ++i) {
          sb.append(similar.get(i));
          sb.append(", ");
        }

        sb.append("or ");
        sb.append(similar.get(similar.size() - 1));
        sb.append("?");

      }

    }

    throw new IllegalArgumentException(sb.toString());

  }

  @Override
  public String toString() {
    return "output type " + this.typeName;
  }

}
