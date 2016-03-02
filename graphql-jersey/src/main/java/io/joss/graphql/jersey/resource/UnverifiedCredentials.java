package io.joss.graphql.jersey.resource;

import java.util.List;

import io.joss.graphql.jersey.auth.RegistryAuthValue;
import lombok.ToString;

@ToString
public class UnverifiedCredentials
{

  private List<RegistryAuthValue> values;

  public UnverifiedCredentials(List<RegistryAuthValue> values)
  {
    this.values = values;
  }

}
