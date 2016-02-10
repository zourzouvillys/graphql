package io.joss.graphql.jersey;


import io.joss.graphql.jersey.auth.RegistryAuthValue;
import io.joss.graphql.jersey.auth.RegistryBasicAuthValue;
import io.joss.graphql.jersey.auth.RegistryBearerAuthValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistryHttpUtils
{

  // note: these must be lowercase as they are used in a switch statement below
  private static final String BASIC = "basic";
  
  // note: these must be lowercase as they are used in a switch statement below
  private static final String BEARER = "bearer";

  public static RegistryAuthValue parseAuth(String auth)
  {

    if (auth == null)
    {
      return null;
    }

    String[] parts = auth.trim().split(" ", 2);

    if (parts.length != 2)
    {
      return null;
    }

    switch (parts[0].toLowerCase())
    {

      case BASIC:
        return RegistryBasicAuthValue.fromEncoded(parts[1]);

      case BEARER:
        return RegistryBearerAuthValue.fromToken(parts[1]);

      default:
        log.info("Unknown auth scheme: '{}'", parts[0]);

    }

    return null;

  }

}
 