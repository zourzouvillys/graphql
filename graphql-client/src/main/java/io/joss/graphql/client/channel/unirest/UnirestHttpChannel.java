package io.joss.graphql.client.channel.unirest;

import java.util.LinkedHashMap;
import java.util.Map;

import io.joss.graphql.client.runtime.GQLChannel;
import io.joss.graphql.client.runtime.GQLExecution;
import io.joss.graphql.client.runtime.GQLPreparedStatement;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.value.GQLObjectValue;
import lombok.Getter;

public class UnirestHttpChannel implements GQLChannel
{

  @Getter
  private String url;
  
  @Getter
  private Map<String, String> headers;

  public UnirestHttpChannel(UnirestHttpChannelBuilder builder)
  {
    this.url = builder.getUrl();
    this.headers = new LinkedHashMap<>(builder.getHeaders());
  }

  /**
   * REST endpoints do not support preparing of statements.
   */

  @Override
  public GQLPreparedStatement prepare(GQLDocument doc)
  {
    return new EmulatedPreparedStatement(this, doc);
  }

  /**
   * 
   */
  
  @Override
  public GQLExecution execute(GQLDocument doc, String name, GQLObjectValue input)
  {
    return new UnirestExecution(this, doc, name, input);
  }

}
