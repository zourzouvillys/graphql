package io.zrz.graphql.zulu.doc;

import java.util.List;
import java.util.Optional;

import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.zulu.types.ZAnnotation;
import io.zrz.zulu.types.ZStructType;
import io.zrz.zulu.values.ZStructValue;

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

  /**
   * resolves the provided arguments for this prepared selection, returning the {@link ZStructValue} representing it.
   * 
   * the type of the instance will be the same as the {@link #parameters()} return value.
   * 
   */

  Optional<ZStructValue> arguments(GQLVariableProvider provider);

  /**
   * the position in the source input that the field name was declared.
   */

  GQLSourceLocation sourceLocation();

  /**
   * a qualified path to the root selection.
   */

  String path();

  /**
   * true if this selection is a leaf (has no child selections).
   */

  default boolean isLeaf() {
    return subselections().isEmpty();
  }

}
