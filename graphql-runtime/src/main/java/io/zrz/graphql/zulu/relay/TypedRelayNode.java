package io.zrz.graphql.zulu.relay;

public interface TypedRelayNode<T extends TypedRelayNode<T>> extends RelayNode {

  default NodeRef<T> ref() {
    return NodeRef.from((T) this);
  }

}
