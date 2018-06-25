package io.zrz.graphql.zulu.engine;

public enum ZuluWarningCategory {

  /**
   * a transport related error - e.g, cache key missing.
   */

  TRANSPORT,

  /**
   * application related category.
   */

  APPLICATION,

  /**
   * there was a problem with the syntax of the query.
   */

  SYNTAX,

  /**
   * something was wrong with the selection; an invalid field selected, missing required input parameter, or a selection
   * on a leaf.
   */

  SELECTION,

  /**
   * an error with the operation invocation, e.g invalid operation name or trying to invoke a mutation in an immutable
   * operation (a GET).
   */

  OPERATION,

  /**
   * there was a missing variable, or there was an error with input data. this may be a type mismatch (providing a
   * string that couldn't be converted to an integer) or null value when the input is marked as non-null.
   */

  INPUT,

  /**
   * the handler for a field threw an exception or did not return a value when one was expected.
   */

  HANDLER,

  /**
   * there was an internal error in the engine
   */

  INTERNAL

}
