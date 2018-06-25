package io.zrz.zulu.schema.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.zulu.schema.binding.BoundElementVisitor;
import io.zrz.zulu.schema.binding.BoundFieldSelection;
import io.zrz.zulu.schema.binding.BoundFragment;
import io.zrz.zulu.schema.binding.BoundLeafSelection;
import io.zrz.zulu.schema.binding.BoundObjectSelection;
import io.zrz.zulu.schema.binding.BoundOperation;
import io.zrz.zulu.schema.binding.BoundSelection;

/**
 * calculates the model return type based on the structure of the query.
 *
 * @author theo
 *
 */

public class ModelExtractor implements BoundElementVisitor.SupplierVisitor<ModelElement> {

  @Override
  public ModelElement visitOperation(final BoundOperation op) {
    // if (op.selections().size() == 1) {
    // return op.selections().get(0).accept(this);
    // }
    return new ModelRootType(op, op.selections()
        .stream()
        .map(x -> x.accept(this))
        .collect(ImmutableList.toImmutableList()));
  }

  @Override
  public ModelElement visitObject(final BoundObjectSelection obj) {

    final ModelConnectionMeta connectionMeta = ModelTraits.isConnectionLike(obj.selectionType());

    if (connectionMeta != null && false) {

      // it's a connection so assign the selections from all three into the right place.

      return new ModelConnection(
          obj,
          connectionMeta,
          connectionMeta
              .connectionSelections(obj.selections())
              .collect(ImmutableList.toImmutableList()),
          connectionMeta
              .edgeSelection(obj.selections())
              .collect(ImmutableList.toImmutableList()),
          connectionMeta
              .nodeSelection(obj.selections())
              .map(sel -> sel.accept(this))
              .collect(ImmutableList.toImmutableList())
      //
      );

    }

    // if (obj.selections().size() == 1) {
    // return obj.selections().get(0).accept(this);
    // }

    final ImmutableMap.Builder<String, ModelElement> fields = ImmutableMap.builder();

    for (final BoundSelection sel : obj.selections()) {

      if (sel instanceof BoundFieldSelection) {

        final String fieldName = ((BoundFieldSelection) sel).outputName();
        final ModelElement element = sel.accept(this);

        fields.put(fieldName, element);

      }

    }

    return new ModelObjectType(obj, fields.build());
  }

  @Override
  public ModelElement visitLeaf(final BoundLeafSelection leaf) {
    return new ModelScalarField(leaf);
  }

  @Override
  public ModelElement visitFragment(final BoundFragment frag) {
    frag.selections().forEach(s -> s.accept(this));
    throw new IllegalArgumentException();
    // return null;
  }

}
