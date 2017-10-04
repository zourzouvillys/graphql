package io.zrz.graphql.jersey.resource;

import io.zrz.graphql.jersey.auth.RegistryAuthValue;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GraphQLHttpParams
{
  private RegistryAuthValue auth;
}
