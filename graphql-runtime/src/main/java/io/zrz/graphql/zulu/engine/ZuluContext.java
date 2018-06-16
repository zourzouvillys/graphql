package io.zrz.graphql.zulu.engine;

import java.util.Map;

public interface ZuluContext {

  /**
   * execute a single field.
   * 
   * @param fieldName
   *          The output field name to execute
   * 
   * @return The value
   */

  ZuluExecutionResult execute(ZuluRequest req, ZuluResultReceiver receiver, String fieldName);

  /**
   * execute all fields (potentially in parallel if it is a query) and return the result as a map of output field names
   * and their values.
   */

  ZuluExecutionResult execute(ZuluRequest req, ZuluResultReceiver res);

  /**
   * helper method which executes using a default receiver and returns the values directly.
   */

  default Map<ZuluSelection, Object> execute() {
    MapCollectingZuluResultsReceiver collector = new MapCollectingZuluResultsReceiver();
    execute(new DefaultZuluRequest(), collector);
    return collector.result();
  }

  /**
   * helper method which executes using a default receiver and returns the values directly.
   */

  default Object execute(String fieldName) {
    MapCollectingZuluResultsReceiver collector = new MapCollectingZuluResultsReceiver();
    execute(new DefaultZuluRequest(), collector, fieldName);
    return collector.result();
  }

  default ZuluExecutionResult execute(ZuluResultReceiver results) {
    return execute(new DefaultZuluRequest(), results);
  }

}
