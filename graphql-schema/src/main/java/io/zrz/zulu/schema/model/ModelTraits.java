package io.zrz.zulu.schema.model;

import java.util.stream.Stream;

import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.ResolvedObjectOrInterfaceType;
import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.TypeUse;
import io.zrz.zulu.schema.binding.BoundElementVisitor.SupplierVisitor;
import io.zrz.zulu.schema.binding.BoundFragment;
import io.zrz.zulu.schema.binding.BoundInlineFragment;
import io.zrz.zulu.schema.binding.BoundLeafSelection;
import io.zrz.zulu.schema.binding.BoundNamedFragment;
import io.zrz.zulu.schema.binding.BoundObjectSelection;
import io.zrz.zulu.schema.binding.BoundOperation;
import io.zrz.zulu.schema.binding.BoundSelection;
import io.zrz.zulu.schema.binding.BoundSelection.PredicateVisitor;

public class ModelTraits {

  /**
   * true if this type looks like it could be a connection. it will:
   * 
   * <ul>
   * <li>have a field called "edges"</li>
   * <li>have a "pageInfo"</li>
   * <li>have a node type which implements "Node"</li>
   * </ul>
   * 
   * @param type
   * @return
   */

  public static ModelConnectionMeta isConnectionLike(ResolvedType type) {

    ResolvedObjectOrInterfaceType ooif = ModelUtils.asObjectOrInterface(type);

    if (ooif == null) {
      return null;
    }

    ResolvedObjectField edgesField = ooif.field("edges");

    if (edgesField == null) {
      return null;
    }

    ResolvedObjectField pageInfoField = ooif.field("pageInfo");

    if (pageInfoField == null) {
      return null;
    }

    TypeUse edgesTypeRef = edgesField.returnType();

    ResolvedObjectOrInterfaceType edgeType = ModelUtils.asObjectOrInterface(edgesTypeRef.targetType());

    if (edgeType == null) {
      return null;
    }

    ResolvedObjectField nodeField = edgeType.field("node");

    if (nodeField == null) {
      return null;
    }

    ResolvedObjectOrInterfaceType nodeType = ModelUtils.asObjectOrInterface(nodeField.returnType().targetType());

    if (nodeType == null) {
      return null;
    }

    if (!ModelUtils.isAssignableTo(nodeType, "Node")) {
      return null;
    }

    return new ModelConnectionMeta(
        ooif,
        edgesField,
        edgeType,
        nodeField,
        nodeType);

  }

  public static PredicateVisitor fieldSelection(ResolvedObjectField edgeField) {
    return new PredicateVisitor() {

      @Override
      public boolean apply(BoundNamedFragment sel) {
        return false;
      }

      @Override
      public boolean apply(BoundInlineFragment sel) {
        return false;
      }

      @Override
      public boolean apply(BoundObjectSelection sel) {
        return sel.fieldSelection().equals(edgeField);
      }

      @Override
      public boolean apply(BoundLeafSelection sel) {
        return sel.field().equals(edgeField);
      }

    };
  }

  public static SupplierVisitor<Stream<BoundSelection>> childSelections() {
    return new SupplierVisitor<Stream<BoundSelection>>() {

      @Override
      public Stream<BoundSelection> visitOperation(BoundOperation op) {
        return op.selections().stream();
      }

      @Override
      public Stream<BoundSelection> visitObject(BoundObjectSelection selection) {
        return selection.selections().stream();
      }

      @Override
      public Stream<BoundSelection> visitLeaf(BoundLeafSelection leaf) {
        return Stream.empty();
      }

      @Override
      public Stream<BoundSelection> visitFragment(BoundFragment fragment) {
        return Stream.empty();
      }

    };
  }

}
