package io.zrz.graphql.client.runtime;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface RelayCollection<C, E, N> extends Iterable<N>
{

  default Stream<N> stream()
  {
    return StreamSupport.stream(spliterator(), false);
  }

}
