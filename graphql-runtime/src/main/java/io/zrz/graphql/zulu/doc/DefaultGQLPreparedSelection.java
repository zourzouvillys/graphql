package io.zrz.graphql.zulu.doc;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZStructValue;
import io.zrz.zulu.values.ZStructValueBuilder;
import io.zrz.zulu.values.ZValue;

class DefaultGQLPreparedSelection implements GQLPreparedSelection {

  private static Logger log = LoggerFactory.getLogger(DefaultGQLPreparedSelection.class);

  private final GQLSelectionTypeCriteria condition;
  private final GQLFieldSelection selection;
  private final DefaultGQLPreparedOperation req;
  private final DefaultGQLPreparedSelection parent;

  public DefaultGQLPreparedSelection(final DefaultGQLPreparedOperation req, final DefaultGQLPreparedSelection parent, final GQLSelectionTypeCriteria condition,
      final GQLFieldSelection selection) {
    this.req = req;
    this.parent = parent;
    this.condition = condition;
    this.selection = selection;
  }

  @Override
  public GQLSourceLocation sourceLocation() {
    return this.selection.location();
  }

  @Override
  public List<ZAnnotation> annotations() {
    return this.selection.directives().stream().map(x -> new ZAnnotation() {

      @Override
      public Optional<ZStructValue> value() {
        return null;
      }

      @Override
      public String name() {
        return x.name();
      }

    })
        .collect(Collectors.toList());
  }

  @Override
  public Optional<GQLSelectionTypeCriteria> typeCritera() {
    return Optional.ofNullable(this.condition);
  }

  @Override
  public String fieldName() {
    return this.selection.name();
  }

  @Override
  public String outputName() {
    return this.selection.outputName();
  }

  @Override
  public List<DefaultGQLPreparedSelection> subselections() {
    return this.selection
        .selections()
        .stream()
        .sequential()
        .flatMap(x -> x.apply(new SelectionGenerator(this.req, this)))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    String id = this.path();

    if (this.parameters().isPresent()) {
      id += this.parameters()
          .get()
          .fields()
          .entrySet()
          .stream()
          .map(x -> x.getKey() + ": " + x.getValue())
          .collect(Collectors.joining(", ", "(", ")"));
    }

    if (this.condition != null) {
      return id + " " + this.condition;
    }
    return id;
  }

  @Override
  public String path() {
    if (this.parent == null) {
      return this.outputName();
    }
    return this.parent.path() + "." + this.outputName();
  }

  /**
   *
   */

  private final static class ParamField implements ZField, RuntimeParameterHolder {

    private final DefaultGQLPreparedOperation req;
    private final GQLArgument arg;

    public ParamField(final DefaultGQLPreparedOperation req, final GQLArgument arg) {
      this.req = req;
      this.arg = arg;
    }

    @Override
    public ZTypeUse fieldType() {
      return ValueResolvingVisitor.create(this.req, this.arg.value()).type();
    }

    @Override
    public Optional<ZValue> defaultValue() {
      return this.arg.value().apply(new DefaultZValueValueExtractor(this.req, this.arg));
    }

    @Override
    public Optional<ZValue> constantValue() {
      return this.arg.value().apply(new ConstantZValueValueExtractor(this.req, this.arg, null));
    }

    @Override
    public String toString() {
      return this.fieldType() + Optional.ofNullable(this.constantValue().orElse(this.defaultValue().orElse(null))).map(val -> " = " + val).orElse("");
    }

    public Optional<ZValue> resolve(final GQLVariableProvider provider) {
      return this.arg.value().apply(new ConstantZValueValueExtractor(this.req, this.arg, provider));
    }

    @Override
    public String parameterName() {
      return this.arg.value().apply(new VariableNameExtractor(this.req, this.arg));
    }

  }

  /**
   * each of the arguments for this selection node (if any) along with the type.
   *
   * @return
   */

  @Override
  public Optional<ZStructType> parameters() {

    if (this.selection.args().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new ZStructType() {

      @Override
      public Map<String, ZField> fields() {
        return DefaultGQLPreparedSelection.this.selection.args()
            .stream()
            .collect(Collectors.toMap(x -> x.name(), x -> new ParamField(DefaultGQLPreparedSelection.this.req, x)));
      }

      @Override
      public String toString() {
        return this.fields()
            .entrySet()
            .stream()
            .map(x -> x.getKey() + ": " + x.getValue())
            .collect(Collectors.joining(", ", "(", ")"));
      }

    });

  }

  /**
   * given a set of input arguments, returns the resolved parameter list for this selection.
   */

  @Override
  public Optional<ZStructValue> arguments(final GQLVariableProvider provider) {
    return this.parameters().map(struct -> this.resolve(struct, provider));
  }

  /**
   *
   * @param struct
   * @param provider
   * @return
   */

  private ZStructValue resolve(final ZStructType struct, final GQLVariableProvider provider) {

    final ZStructValueBuilder b = new ZStructValueBuilder(struct);

    for (final Entry<String, ? extends ZField> e : struct.fields().entrySet()) {

      // constant values don't need anything done. already set.
      if (e.getValue().constantValue().isPresent()) {
        continue;
      }

      //

      final ParamField field = (ParamField) e.getValue();

      if (field == null) {
        return null;
      }

      final ZValue value = field
          .resolve(provider)
          .orElseGet(() -> e.getValue().defaultValue().orElse(null));

      if (value == null) {
        log.warn("resolved field {}", e);
      }

      b.put(e.getKey(), value);

    }

    return b.build();
  }

}
