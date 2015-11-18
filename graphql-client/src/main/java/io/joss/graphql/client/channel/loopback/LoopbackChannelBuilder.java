package io.joss.graphql.client.channel.loopback;

/**
 * A builder for a channel which invokes the given instance each time a request is received.
 * 
 * This is the same as performing an end to end query via a remote transport, just the query is locally implemented.
 * 
 */

public class LoopbackChannelBuilder<Q, M, S>
{

  private Class<Q> type;
  private Q instance;

  public LoopbackChannelBuilder(Q instance, Class<Q> type)
  {
    this.instance = instance;
    this.type = type;
  }

  public LoopbackChannel<Q> build()
  {
    return new LoopbackChannel<>(type, instance);
  }

  public static <Q> LoopbackChannelBuilder<Q, Void, Void> forInstance(Q instance, Class<Q> type)
  {
    return new LoopbackChannelBuilder<Q, Void, Void>(instance, type);
  }
  
  public static <Q> LoopbackChannelBuilder<Q, Void, Void> forInstance(Q instance)
  {
    return forInstance(instance, (Class<Q>)instance.getClass());
  }

}
