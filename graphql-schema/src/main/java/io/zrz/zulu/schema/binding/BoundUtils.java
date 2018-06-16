package io.zrz.zulu.schema.binding;

import io.zrz.graphql.core.doc.GQLSelection;

public class BoundUtils {

  static BoundSelection bind(BoundSelectionContainer parent, GQLSelection sel, BoundBuilder b) {
    return sel.apply(b.selector(), parent);
  }

}
