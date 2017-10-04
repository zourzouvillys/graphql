package io.zrz.graphql.client.channel.unirest;

import java.util.LinkedHashMap;
import java.util.Map;

import io.zrz.graphql.client.channel.AbstractChannelImplBuilder;
import io.zrz.graphql.client.runtime.GQLChannel;
import lombok.Getter;

/**
 * A channel which sends it's queries as HTTP.
 * 
 * Does not support subscriptions.
 * 
 * @author theo
 *
 */

public class UnirestHttpChannelBuilder extends AbstractChannelImplBuilder<UnirestHttpChannelBuilder>
{

  @Getter
  private String url;

  @Getter
  private Map<String, String> headers = new LinkedHashMap<>();

  private UnirestHttpChannelBuilder(String url)
  {
    this.url = url;
  }
  

  public UnirestHttpChannelBuilder withHeader(String name, String value)
  {
    headers.put(name, value);
    return this;
  }


  /**
   * Returns a channel which points to the specified endpoint. 
   */
  
  @Override
  public GQLChannel build()
  {
    return new UnirestHttpChannel(this);
  }
  

  /**
   * Constructs a new builder for executing queries against the given URL.
   * 
   * @param url
   * 
   * @return
   * 
   */

  public static UnirestHttpChannelBuilder forUrl(String url)
  {
    return new UnirestHttpChannelBuilder(url);
  }

}
