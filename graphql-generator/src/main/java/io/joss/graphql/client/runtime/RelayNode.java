package io.joss.graphql.client.runtime;

/**
 * tag annotation to indicate that this class is a relay node, so can be fetched.
 * 
 * @author theo
 *
 */

public interface RelayNode
{
  
  String id();
  
}
