package io.zrz.graphql.zulu.engine;

import java.util.List;

public interface ZuluExecutionResult {

  /**
   * a collection of notes related to the execution. these may be errors, warnings, or other metainfo.
   */

  List<ZuluWarning> notes();

}
