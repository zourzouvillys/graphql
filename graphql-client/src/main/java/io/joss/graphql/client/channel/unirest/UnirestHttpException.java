package io.joss.graphql.client.channel.unirest;

import java.util.HashMap;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Indicates a HTTP error.
 * 
 * @author theo
 *
 */

@Value
@EqualsAndHashCode(callSuper = false)
public class UnirestHttpException extends RuntimeException
{

  /**
   * 
   */
  
  private static final long serialVersionUID = 1L;

  /**
   * 
   */
  
  private final int status;
  private final String text;
  private final HashMap<String, List<String>> headers;
  private final String body;

}
