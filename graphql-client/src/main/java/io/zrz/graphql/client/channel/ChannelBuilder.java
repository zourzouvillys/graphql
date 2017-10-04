package io.zrz.graphql.client.channel;

import io.zrz.graphql.client.runtime.GQLChannel;

public interface ChannelBuilder<T extends ChannelBuilder<T>>
{

  GQLChannel build();

}
