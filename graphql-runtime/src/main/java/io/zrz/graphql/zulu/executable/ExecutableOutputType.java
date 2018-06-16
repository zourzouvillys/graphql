package io.zrz.graphql.zulu.executable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.FuzzyScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.ZOutputType;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder.Symbol;
import io.zrz.zulu.types.ZTypeKind;

public final class ExecutableOutputType implements ExecutableType, ZOutputType, ExecutableElement {

  private static Logger log = LoggerFactory.getLogger(ExecutableOutputType.class);

  private ExecutableSchema schema;
  private String typeName;
  private ImmutableMap<String, ExecutableOutputField> fields;
  private ExecutableTypeUse typeUse;
  private TypeToken<?> javaType;
  private String documentation;

  ExecutableOutputType(ExecutableSchema schema, Symbol symbol, BuildContext buildctx) {

    buildctx.add(symbol, this);

    this.schema = schema;
    this.typeName = symbol.typeName;
    this.javaType = symbol.typeToken;

    this.documentation = symbol.handle
        .analysis()
        .annotations(GQLDocumentation.class)
        .stream().map(a -> a.value())
        .collect(Collectors.joining("\n\n"));

    this.fields = symbol.handle.outputFields(buildctx.filterFor(this))
        // .peek(s -> log.info("field {} -> {}", s.fieldName(), s))
        .map(field -> new ExecutableOutputField(this, symbol, field, buildctx))
        .collect(ImmutableMap.toImmutableMap(k -> k.fieldName(), k -> k));

    this.typeUse = buildctx.use(this, symbol.typeToken, 0);

  }

  public ExecutableSchema schema() {
    return this.schema;
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
    return typeUse;
  }

  /**
   * the java type that this output type represents.
   */

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
  public Optional<ExecutableOutputField> field(String name) {
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

  public List<String> similarFields(String fieldName, int count) {
    return fieldNames()
        .filter(name -> SCORE.fuzzyScore(fieldName, name) > 0)
        .sorted((a, b) -> Integer.compare(SCORE.fuzzyScore(fieldName, b), SCORE.fuzzyScore(fieldName, b)))
        .limit(count)
        .collect(Collectors.toList());
  }

  public IllegalArgumentException missingFieldException(String fieldName) {

    StringBuilder sb = new StringBuilder();

    sb.append("field ");
    sb.append(typeName());
    sb.append(".");
    sb.append(fieldName);
    sb.append(" does not exist.");

    List<String> similar = similarFields(fieldName, 5);

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
