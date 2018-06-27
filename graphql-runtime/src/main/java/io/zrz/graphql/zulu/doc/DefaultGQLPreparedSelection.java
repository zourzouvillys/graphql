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
import io.zrz.zulu.values.ZValueProvider;

class DefaultGQLPreparedSelection implements GQLPreparedSelection {

  private static Logger log = LoggerFactory.getLogger(DefaultGQLPreparedSelection.class);

  private final GQLSelectionTypeCriteria condition;
  private final GQLFieldSelection selection;
  private final DefaultGQLPreparedOperation req;
  private final DefaultGQLPreparedSelection parent;
  private final List<ZAnnotation> annotations;
  private final List<DefaultGQLPreparedSelection> subselections;
  private final Optional<InputFieldStruct> parametersType;

  public DefaultGQLPreparedSelection(
      final DefaultGQLPreparedOperation req,
      final DefaultGQLPreparedSelection parent,
      final GQLSelectionTypeCriteria condition,
      final GQLFieldSelection selection) {

    this.req = req;
    this.parent = parent;
    this.condition = condition;
    this.selection = selection;
    this.annotations = this.selection.directives().stream().map(x -> new ZAnnotation() {

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

    this.subselections = this.selection
        .selections()
        .stream()
        .sequential()
        .flatMap(x -> x.apply(new SelectionGenerator(this.req, this)))
        .collect(Collectors.toList());

    if (this.selection.args().isEmpty()) {
      this.parametersType = Optional.empty();
    }
    else {
      this.parametersType = Optional.of(new InputFieldStruct());
    }

  }

  @Override
  public GQLSourceLocation sourceLocation() {
    return this.selection.location();
  }

  public void validate() {

  }

  @Override
  public List<ZAnnotation> annotations() {
    return this.annotations;
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
    return this.subselections;
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
    private final ZValueProvider provider;
    private final Optional<ZValue> constantValue;
    private final Optional<ZValue> defaultValue;

    public ParamField(final DefaultGQLPreparedOperation req, final GQLArgument arg) {
      this.req = req;
      this.arg = arg;
      this.provider = ValueResolvingVisitor.create(req, arg.value());
      this.constantValue = this.arg.value().apply(new ConstantZValueValueExtractor(this.req, this.arg, null));
      this.defaultValue = this.arg.value().apply(new DefaultZValueValueExtractor(this.req, this.arg));
    }

    @Override
    public ZTypeUse fieldType() {
      return this.provider.type();
    }

    @Override
    public Optional<ZValue> defaultValue() {
      return this.defaultValue;
    }

    @Override
    public Optional<ZValue> constantValue() {
      return this.constantValue;
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
      return this.arg.value().apply(new VariableNameExtractor());
    }

    public void validate(final GQLPreparedValidationListener listener) {

      if (this.provider == null) {
        listener.error(this, this.arg.location(), "variable '$" + this.parameterName() + "' not defined");
      }

    }

  }

  private class InputFieldStruct implements ZStructType {

    private final Map<String, ParamField> fields;

    InputFieldStruct() {
      this.fields = DefaultGQLPreparedSelection.this.selection.args()
          .stream()
          .collect(Collectors.toMap(x -> x.name(), x -> new ParamField(DefaultGQLPreparedSelection.this.req, x)));
    }

    @Override
    public Map<String, ParamField> fields() {
      return this.fields;
    }

    @Override
    public String toString() {
      return this.fields()
          .entrySet()
          .stream()
          .map(x -> x.getKey() + ": " + x.getValue())
          .collect(Collectors.joining(", ", "(", ")"));
    }

    public void validate(final GQLPreparedValidationListener listener) {

      this.fields.values().forEach(param -> param.validate(listener));

    }

  }

  /**
   * each of the arguments for this selection node (if any) along with the type.
   *
   * @return
   */

  @Override
  public Optional<InputFieldStruct> parameters() {
    return this.parametersType;
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

  public void validate(final GQLPreparedValidationListener listener) {
    this.parametersType.ifPresent(val -> val.validate(listener));
    this.subselections.forEach(sel -> sel.validate(listener));

  }

}
