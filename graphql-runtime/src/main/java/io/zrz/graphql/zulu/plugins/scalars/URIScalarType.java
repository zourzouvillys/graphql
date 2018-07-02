package io.zrz.graphql.zulu.plugins.scalars;

import java.net.URI;

import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.zulu.api.GQLScalarHandler;

public class URIScalarType implements GQLScalarHandler<URI> {

  @Override
  public URI readValue(final GQLValue value) {
    return URI.create(value.toString());
  }

}
