package io.zrz.graphql.zulu.engine;

/**
 * consumer facing API to receive results from execution.
 * 
 * @author theo
 */

public interface ZuluResultReceiver {

  /**
   * called every time a node in the selection set is entered, before invocation of it.
   * 
   * @param ctx
   *          The context that has been entered.
   */

  void push(ZuluSelectionContainer container, Object instance);

  /**
   * called every time a selection field is left, after all children have been processed.
   */

  void pop(ZuluSelectionContainer container, Object instance);

  /**
   * called each time an instance context has been entered.
   * 
   * when processing an array, this will be called for each instance before {@link ZuluResultReceiver#pop()} is called.
   * So the calls would be push(array_field), next(a), next(b), next(c), pop().
   * 
   * note that further pushes may occur if the selection has sub selections.
   * 
   * @param instance
   *          the underlying java instance (or instance token).
   */

  void next(Object instance);

  ///
  /// --- [ fields ] ---
  ///

  /**
   * called when a field is missing a value, i.e null.
   * 
   * default values will be provided as the type of the default, so this will only be called when there is a missing
   * value.
   * 
   */

  void write(ZuluSelection field);

  /**
   * for writing out primitive values.
   */

  void write(ZuluSelection field, int value);

  /**
   * for writing out primitive values.
   */

  void write(ZuluSelection field, long value);

  /**
   * for writing out primitive values.
   */

  void write(ZuluSelection field, boolean value);

  /**
   * for writing out primitive values.
   */

  void write(ZuluSelection field, double value);

  /**
   * for writing out of string values.
   */

  void write(ZuluSelection field, String value);

  /**
   * for all other field values, they are passed in an objects.
   */

  void write(ZuluSelection field, Object value);

}
