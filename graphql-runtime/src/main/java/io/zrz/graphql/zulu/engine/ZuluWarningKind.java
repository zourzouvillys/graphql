package io.zrz.graphql.zulu.engine;

import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.HANDLER;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.INPUT;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.INTERNAL;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.OPERATION;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.SELECTION;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.SYNTAX;

public enum ZuluWarningKind {

  /**
   * 
   */

  SYNTAX_ERROR(
      SYNTAX,
      "query syntax error"),

  /**
   * 
   */

  INVALID_OPERATION(
      OPERATION,
      "invalid operation name '${operation.name}'"),

  /**
   * 
   */

  OPERATION_NAME_REQUIRED(
      OPERATION,
      "document does not contain a default query operation, so operationName must be provided."),

  /**
   * An unknown field was requested.
   */

  UNKNOWN_FIELD(
      SELECTION,
      "field '${field.name}' does not exist on type '${type.name}'"),

  /**
   * a required parameter was missing in the query.
   */

  MISSING_PARAMETER(SELECTION),

  /**
   * the input type provided did not match the type expected, and it is not possible to perform a conversion.
   */

  INCOMPATIBLE_TYPE(
      INPUT),

  /**
   * a selection on a non-leaf field without a subselection.
   */

  NONLEAF_SELECTION(
      SELECTION,
      "field '${type.name}.${field.name}' is of non scalar type '${field.type.name}' so must have a selection of subfields. did you mean '${field.name} { ... }'?"),

  /**
   * a sub-selection on a leaf
   */

  LEAF_SELECTION(
      SELECTION,
      "field '${type.name}.${field.name}' must not have a selection since '${field.type.name}' has no subfields."),

  /**
   * a handler threw an exception
   */

  INVOCATION_EXCEPTION(
      HANDLER,
      "the handler for '${field.name}' threw an exception"),

  /**
   * an unsupported feature was required.
   */

  NOT_SUPPORTED(
      INTERNAL,
      "unsupported feature"),

  /**
   * an internal error occured
   */

  INTERNAL_ERROR(
      INTERNAL,
      "an internal error occured while processing"),

  /**
   * 
   */

  INVALID_HANDLER(
      HANDLER,
      "field '${field.name}' in type '${type.name}' is not currently available."),

  ;

  private ZuluWarningCategory category;
  private String template;

  ZuluWarningKind(ZuluWarningCategory category) {
    this.category = category;
  }

  ZuluWarningKind(ZuluWarningCategory category, String template) {
    this.category = category;
    this.template = template;
  }

  /**
   * the category of warning.
   */

  public ZuluWarningCategory category() {
    return this.category;
  }

  /**
   */

  public String detail(ZuluWarning warning) {
    if (this.template != null) {
      return ZuluWarnings.format(warning, template);
    }
    return toString();
  }

  /**
   * 
   */

  @Override
  public String toString() {
    return category + ":" + name();
  }

}
