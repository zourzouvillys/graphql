package io.zrz.graphql.zulu.engine;

import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.HANDLER;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.INPUT;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.INTERNAL;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.OPERATION;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.SELECTION;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.SYNTAX;
import static io.zrz.graphql.zulu.engine.ZuluWarningCategory.TRANSPORT;

public enum ZuluWarningKind {

  /**
   *
   */

  PERSISTED_QUERY_NOT_FOUND(
      TRANSPORT,
      "PersistedQueryNotFound"),

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
   * An unknown type was requested (e.g, for a spread).
   */

  UNKNOWN_TYPE(
      SELECTION,
      "type '${symbol}' does not exist"),

  /**
   * an unknown parameter was provided
   */

  UNKNOWN_PARAMETER(SELECTION),

  /**
   * a required parameter was missing in the query.
   */

  MISSING_PARAMETER(SELECTION),

  /**
   * a required variable was missing in the query.
   */

  MISSING_VARIABLE(INPUT),

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
   * invalid spreaad
   */

  INVALID_SPREAD(
      SELECTION,
      "invalid spread type"),

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

  ZuluWarningKind(final ZuluWarningCategory category) {
    this.category = category;
  }

  ZuluWarningKind(final ZuluWarningCategory category, final String template) {
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

  public String detail(final ZuluWarning warning) {
    if (this.template != null) {
      return ZuluWarnings.format(warning, this.template);
    }
    return this.toString();
  }

  /**
   *
   */

  @Override
  public String toString() {
    return this.category + ":" + this.name();
  }

}
