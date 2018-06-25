package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.Optional;

import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.doc.GQLSelectionTypeCriteria;
import io.zrz.graphql.zulu.doc.GQLVariableProvider;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableReceiverType;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZTypeKind;
import io.zrz.zulu.values.ZStructValue;

/**
 * a gql prepared selection bound to the executable model with generated MethodHandle for invoking.
 *
 * @author theo
 *
 */

public abstract class AbstractZuluSelection implements ZuluSelection {

  private final GQLPreparedSelection psel;
  private final ExecutableReceiverType type;
  private final ExecutableOutputField field;

  /**
   *
   * @param exec
   *                The executable builder creating this selection.
   *
   * @param field
   *                The field represented by this selection.
   *
   * @param sel
   *                The raw underlying selection field.
   *
   * @param type
   *                the type that this selection is being made on.
   */

  protected AbstractZuluSelection(final ExecutableOutputField field, final GQLPreparedSelection sel, final ExecutableReceiverType type) {
    this.psel = sel;
    this.type = type;
    this.field = field;
  }

  @Override
  public boolean isList() {
    return this.field.fieldType().arity() > 0;
  }

  @Override
  public String fieldName() {
    return this.psel.fieldName();
  }

  @Override
  public String outputName() {
    return this.psel.outputName();
  }

  @Override
  public Optional<ZStructType> parameters() {
    return this.psel.parameters();
  }

  @Override
  public List<ZAnnotation> annotations() {
    return this.psel.annotations();
  }

  @Override
  public Optional<GQLSelectionTypeCriteria> typeCritera() {
    return this.psel.typeCritera();
  }

  @Override
  public List<? extends GQLPreparedSelection> subselections() {
    return this.psel.subselections();
  }

  @Override
  public Optional<ZStructValue> arguments(final GQLVariableProvider provider) {
    return this.psel.arguments(provider);
  }

  @Override
  public GQLSourceLocation sourceLocation() {
    return this.psel.sourceLocation();
  }

  @Override
  public String path() {
    return this.psel.path();
  }

  @Override
  public ZTypeKind typeKind() {
    return this.type.typeKind();
  }

  @Override
  public LogicalTypeKind logicalKind() {
    return this.type.logicalKind();
  }

  @Override
  public String typeName() {
    return this.type.typeName();
  }

  @Override
  public String toString() {
    return this.psel.toString() + " : " + this.typeName() + " (field: " + this.field.toString() + ")";
  }

  @Override
  public ExecutableTypeUse fieldType() {
    return this.field.fieldType();
  }

  @Override
  public ZField parameter(final String pname) {
    return this.field.parameter(pname);
  }

  @Override
  public boolean isLeaf() {
    return this.psel.isLeaf();
  }

  public ExecutableReceiverType contextType() {
    return this.type;
  }

  @Override
  public ExecutableReceiverType element() {
    return this.type;
  }

}
