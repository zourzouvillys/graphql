package io.zrz.zulu.schema.binding;

import java.util.List;

import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.binding.BoundElementVisitor.SupplierVisitor;

public interface BoundFragment extends BoundSelection, BoundElement {

  /**
   * the type this fragment is spread over.
   */

  ResolvedType spreadType();

  /**
   * the selections in this fragment spread.
   */

  List<BoundSelection> selections();

  @Override
  default <R> R accept(SupplierVisitor<R> visitor) {
    return visitor.visitFragment(this);
  }

}
