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

  private GQLSelectionTypeCriteria condition;
  private GQLFieldSelection selection;
  private DefaultGQLPreparedOperation req;
  private DefaultGQLPreparedSelection parent;

  public DefaultGQLPreparedSelection(DefaultGQLPreparedOperation req, DefaultGQLPreparedSelection parent, GQLSelectionTypeCriteria condition,
      GQLFieldSelection selection) {
    this.req = req;
    this.parent = parent;
    this.condition = condition;
    this.selection = selection;
  }

  @Override
  public GQLSourceLocation sourceLocation() {
    return selection.location();
  }

  @Override
  public List<ZAnnotation> annotations() {
    return selection.directives().stream().map(x -> new ZAnnotation() {

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
    return Optional.empty();
  }

  @Override
  public String fieldName() {
    return selection.name();
  }

  @Override
  public String outputName() {
    return selection.outputName();
  }

  @Override
  public List<DefaultGQLPreparedSelection> subselections() {
    return this.selection
        .selections()
        .stream()
        .sequential()
        .flatMap(x -> x.apply(new SelectionGenerator(req, this)))
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    String id = path();

    if (this.parameters().isPresent()) {
      id += parameters()
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

  private final static class ParamField implements ZField {

    private DefaultGQLPreparedOperation req;
    private GQLArgument arg;

    public ParamField(DefaultGQLPreparedOperation req, GQLArgument arg) {
      this.req = req;
      this.arg = arg;
    }

    @Override
    public ZTypeUse fieldType() {
      return ValueResolvingVisitor.create(req, arg.value()).type();
    }

    @Override
    public Optional<ZValue> defaultValue() {
      return arg.value().apply(new DefaultZValueValueExtractor(req, arg));
    }

    @Override
    public Optional<ZValue> constantValue() {
      return arg.value().apply(new ConstantZValueValueExtractor(req, arg, null));
    }

    @Override
    public String toString() {
      return fieldType() + (Optional.ofNullable(constantValue().orElse(defaultValue().orElse(null))).map(val -> " = " + val).orElse(""));
    }

    public Optional<ZValue> resolve(GQLVariableProvider provider) {
      return arg.value().apply(new ConstantZValueValueExtractor(req, arg, provider));
    }

  }

  /**
   * each of the arguments for this selection node (if any) along with the type.
   * 
   * @return
   */

  @Override
  public Optional<ZStructType> parameters() {

    if (selection.args().isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new ZStructType() {

      @Override
      public Map<String, ZField> fields() {
        return selection.args()
            .stream()
            .collect(Collectors.toMap(x -> x.name(), x -> new ParamField(req, x)));
      }

      @Override
      public String toString() {
        return fields()
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
  public Optional<ZStructValue> arguments(GQLVariableProvider provider) {
    return this.parameters().map(struct -> resolve(struct, provider));
  }

  /**
   * 
   * @param struct
   * @param provider
   * @return
   */

  private ZStructValue resolve(ZStructType struct, GQLVariableProvider provider) {

    ZStructValueBuilder b = new ZStructValueBuilder(struct);

    for (Entry<String, ? extends ZField> e : struct.fields().entrySet()) {

      // constant values don't need anything done. already set.
      if (e.getValue().constantValue().isPresent()) {
        continue;
      }

      //

      ParamField field = (ParamField) e.getValue();

      ZValue value = field
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