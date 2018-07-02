package io.zrz.graphql.zulu.relay;

import org.eclipse.jdt.annotation.NonNull;

/**
 * a typed reference to a node.
 *
 * @author theo
 *
 * @param <T>
 */

public final class NodeRef<T> extends DynamicNodeRef {

  NodeRef(final Class<?> type, final String key) {
    super(type.getSimpleName(), key);
  }

  public static <T extends RelayNode> NodeRef<T> from(@NonNull final T node) {
    return new NodeRef<>(node.getClass(), node.id());
  }

}
