package io.joss.graphql.jersey.resource;

import io.joss.graphql.jersey.auth.RegistryAuthValue;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GraphQLHttpParams
{
  private RegistryAuthValue auth;
}
