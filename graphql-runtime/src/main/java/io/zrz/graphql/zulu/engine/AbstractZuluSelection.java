package io.zrz.graphql.zulu.engine;

import java.util.List;
import java.util.Optional;

import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.zulu.LogicalTypeKind;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.doc.GQLSelectionTypeCriteria;
import io.zrz.graphql.zulu.doc.GQLVariableProvider;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;
import io.zrz.graphql.zulu.executable.ExecutableOutputType;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZField;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.types.ZTypeKind;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZStructValue;

/**
 * a gql prepared selection bound to the executable model with generated MethodHandle for invoking.
 * 
 * @author theo
 *
 */

public abstract class AbstractZuluSelection implements ZuluSelection {

  private GQLPreparedSelection psel;
  private ExecutableOutputType type;
  private ExecutableOutputField field;

  /**
   * 
   * @param exec
   *          The executable builder creating this selection.
   * 
   * @param field
   *          The field represented by this selection.
   * 
   * @param sel
   *          The raw underlying selection field.
   * 
   * @param type
   *          the type that this selection is being made on.
   */

  protected AbstractZuluSelection(ExecutableOutputField field, GQLPreparedSelection sel, ExecutableOutputType type) {
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
    return psel.fieldName();
  }

  @Override
  public String outputName() {
    return psel.outputName();
  }

  @Override
  public Optional<ZStructType> parameters() {
    return psel.parameters();
  }

  @Override
  public List<ZAnnotation> annotations() {
    return psel.annotations();
  }

  @Override
  public Optional<GQLSelectionTypeCriteria> typeCritera() {
    return psel.typeCritera();
  }

  @Override
  public List<? extends GQLPreparedSelection> subselections() {
    return psel.subselections();
  }

  @Override
  public Optional<ZStructValue> arguments(GQLVariableProvider provider) {
    return psel.arguments(provider);
  }

  @Override
  public GQLSourceLocation sourceLocation() {
    return psel.sourceLocation();
  }

  @Override
  public String path() {
    return psel.path();
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
    return psel.toString() + " : " + typeName() + " (field: " + field.toString() + ")";
  }

  @Override
  public ZTypeUse fieldType() {
    return this.field.fieldType();
  }

  @Override
  public ZField parameter(String pname) {
    return this.field.parameter(pname);
  }

  @Override
  public boolean isLeaf() {
    return psel.isLeaf();
  }

  public ExecutableOutputType contextType() {
    return this.type;
  }

  @Override
  public ExecutableElement element() {
    return this.type;
  }

}
