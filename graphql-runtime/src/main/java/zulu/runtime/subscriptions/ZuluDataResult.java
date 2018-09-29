package zulu.runtime.subscriptions;

import io.zrz.graphql.zulu.engine.ZuluResultReceiver;

/**
 * base interface for all events emitted during an execution of any operation.
 *
 * @author theo
 *
 */
public interface ZuluDataResult extends ZuluResult {

  void data(ZuluResultReceiver receiver);

}
