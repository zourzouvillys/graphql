package io.zrz.zulu.schema.binding;

import java.util.List;

import io.zrz.zulu.schema.ResolvedType;

public interface BoundFragment extends BoundSelection {

  /**
   * the type this fragment is spread over.
   */

  ResolvedType spreadType();

  /**
   * the selections in this fragment spread.
   */

  List<BoundSelection> selections();

}
