package io.zrz.graphql.core.runtime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZStructValue;

class DefaultGQLPreparedSelection implements GQLPreparedSelection {

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

  String path() {
    if (this.parent == null) {
      return this.outputName();
    }
    return this.parent.path() + "." + this.outputName();
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
        // TODO Auto-generated method stub
        return selection.args()
            .stream()
            .collect(Collectors.toMap(x -> x.name(), x -> new ZField() {

              @Override
              public ZTypeUse fieldType() {
                return ValueResolvingVisitor.create(req, x.value()).type();
              }

              @Override
              public String toString() {
                return fieldType().toString();
              }

            }));
      }

    });

  }

}
