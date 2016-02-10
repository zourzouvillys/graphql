package io.joss.graphql.jersey.auth;


import lombok.Getter;
import lombok.Value;

@Value
public class RegistryBearerAuthValue implements RegistryAuthValue
{

  @Getter
  private String token;

  RegistryBearerAuthValue(String token)
  {
    this.token = token;
  }

  public static RegistryAuthValue fromToken(String string)
  {
    return new RegistryBearerAuthValue(string);
  }

}