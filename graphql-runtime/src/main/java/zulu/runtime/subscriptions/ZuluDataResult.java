package zulu.runtime.subscriptions;

import java.util.List;

import io.zrz.graphql.zulu.engine.ZuluNotesReceiver;
import io.zrz.graphql.zulu.engine.ZuluResultReceiver;
import io.zrz.graphql.zulu.engine.ZuluWarning;

/**
 * base interface for all events emitted during an execution of any operation.
 *
 * @author theo
 *
 */
public interface ZuluDataResult extends ZuluResult {

  void data(ZuluResultReceiver receiver, ZuluNotesReceiver notes);

  List<ZuluWarning> errors();

}
