package io.joss.graphql.client.channel;

import io.joss.graphql.client.runtime.GQLChannel;

public interface ChannelBuilder<T extends ChannelBuilder<T>>
{

  GQLChannel build();

}
