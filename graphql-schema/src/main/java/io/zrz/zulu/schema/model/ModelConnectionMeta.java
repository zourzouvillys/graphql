package io.zrz.zulu.schema.model;

import java.util.List;
import java.util.stream.Stream;

import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.ResolvedObjectOrInterfaceType;
import io.zrz.zulu.schema.binding.BoundInlineFragment;
import io.zrz.zulu.schema.binding.BoundLeafSelection;
import io.zrz.zulu.schema.binding.BoundNamedFragment;
import io.zrz.zulu.schema.binding.BoundObjectSelection;
import io.zrz.zulu.schema.binding.BoundSelection;
import io.zrz.zulu.schema.binding.BoundSelection.PredicateVisitor;

public class ModelConnectionMeta {

  private ResolvedObjectOrInterfaceType connectionType;
  private ResolvedObjectOrInterfaceType edgeType;
  private ResolvedObjectOrInterfaceType nodeType;
  private ResolvedObjectField edgeField;
  private ResolvedObjectField nodeField;

  public ModelConnectionMeta(
      ResolvedObjectOrInterfaceType connectionType,
      ResolvedObjectField edgesField,
      ResolvedObjectOrInterfaceType edgeType,
      ResolvedObjectField nodeField,
      ResolvedObjectOrInterfaceType nodeType) {
    this.connectionType = connectionType;
    this.edgeField = edgesField;
    this.edgeType = edgeType;
    this.nodeField = nodeField;
    this.nodeType = nodeType;
  }

  public ResolvedObjectOrInterfaceType connectionType() {
    return this.connectionType;
  }

  public ResolvedObjectOrInterfaceType edgeType() {
    return this.edgeType;
  }

  public ResolvedObjectOrInterfaceType nodeType() {
    return this.nodeType;
  }

  /**
   * given a selection on the connection itself, returns the selection which is on the node
   */

  public Stream<BoundSelection> nodeSelection(List<BoundSelection> selections) {

    return selections
        .stream()
        .filter(sel -> sel.apply(ModelTraits.fieldSelection(this.edgeField)))
        .flatMap(sel -> sel.accept(ModelTraits.childSelections()))
        .filter(edgeSelection -> edgeSelection.apply(ModelTraits.fieldSelection(this.nodeField)))
        .flatMap(sel -> sel.accept(ModelTraits.childSelections()));

  }

  /**
   * the selections that are on the connection itself.
   * 
   * @param selections
   * @return
   */

  public Stream<BoundSelection> connectionSelections(List<BoundSelection> selections) {

    return selections.stream()
        .filter(sel -> sel.apply(new PredicateVisitor() {

          @Override
          public boolean apply(BoundLeafSelection sel) {
            return true;
          }

          @Override
          public boolean apply(BoundObjectSelection sel) {
            return !sel.fieldName().equals(ModelConnectionMeta.this.edgeField.fieldName());
          }

          @Override
          public boolean apply(BoundInlineFragment sel) {
            return false;
          }

          @Override
          public boolean apply(BoundNamedFragment sel) {
            return false;
          }

        }));

  }

  public Stream<BoundSelection> edgeSelection(List<BoundSelection> selections) {

    return selections.stream()
        .filter(sel -> sel.apply(new PredicateVisitor() {

          @Override
          public boolean apply(BoundLeafSelection sel) {
            return true;
          }

          @Override
          public boolean apply(BoundObjectSelection sel) {
            return !sel.fieldName().equals(ModelConnectionMeta.this.edgeField.fieldName());
          }

          @Override
          public boolean apply(BoundInlineFragment sel) {
            return false;
          }

          @Override
          public boolean apply(BoundNamedFragment sel) {
            return false;
          }

        }));

  }

}
