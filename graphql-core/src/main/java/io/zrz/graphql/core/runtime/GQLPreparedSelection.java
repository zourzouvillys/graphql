package io.zrz.graphql.core.runtime;

import java.util.List;
import java.util.Optional;

import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZStructType;

/**
 * a single GraphQL selection
 */

public interface GQLPreparedSelection {

  /**
   * the field name.
   */

  String fieldName();

  /**
   * the requested output field name for this selection. this will be the same as the fieldName() value, unless a
   * separate name was provided.
   */

  String outputName();

  /**
   * the parameters provided to this field.
   */

  Optional<ZStructType> parameters();

  /**
   * annotations on the field selection.
   */

  List<ZAnnotation> annotations();

  /**
   * any criteria for this field to be output (e.g, a type restriction due to a spread).
   */

  Optional<GQLSelectionTypeCriteria> typeCritera();

  /**
   * any children selected from this field, if any.
   */

  List<? extends GQLPreparedSelection> subselections();

}
