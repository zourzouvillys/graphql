package io.joss.graphql.client.channel.unirest;

import io.joss.graphql.client.runtime.GQLExecution;
import io.joss.graphql.client.runtime.GQLPreparedStatement;
import io.joss.graphql.core.doc.GQLDocument;
import io.joss.graphql.core.value.GQLObjectValue;

public class EmulatedPreparedStatement implements GQLPreparedStatement
{

  private UnirestHttpChannel unirest;
  private GQLDocument doc;

  public EmulatedPreparedStatement(UnirestHttpChannel unirest, GQLDocument doc)
  {
    this.unirest = unirest;
    this.doc = doc;
  }

  /**
   * Pass this execution back to the unirest channel.
   * 
   * @param named
   * @param input
   * @return
   */
  
  @Override
  public GQLExecution execute(String named, GQLObjectValue input)
  {
    return unirest.execute(doc, named, input);
  }

}
