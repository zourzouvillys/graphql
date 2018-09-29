package io.zrz.graphql.zulu.engine;

import java.util.List;

/**
 * a fully bound operation which will provide results once it is subscribed to.
 */

public interface ZuluExecutionResult {

  /**
   * a collection of notes related to the execution. these may be errors, warnings, or other metainfo.
   */

  List<ZuluWarning> notes();

}
