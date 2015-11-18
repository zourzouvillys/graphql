package io.joss.graphql.client.channel.unirest;

import com.mashape.unirest.http.Headers;

import lombok.Value;

@Value
public class GQLHttpException extends RuntimeException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final int status;
  private final String text;
  private final Headers headers;
  private final String body;

}
