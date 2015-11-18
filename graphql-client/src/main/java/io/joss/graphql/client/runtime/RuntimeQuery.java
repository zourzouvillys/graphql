package io.joss.graphql.client.runtime;

import java.util.function.Consumer;

public interface RuntimeQuery<R>
{

  R execute();
  
  
}
