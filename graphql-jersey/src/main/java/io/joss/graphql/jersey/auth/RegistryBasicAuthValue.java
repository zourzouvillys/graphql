package io.joss.graphql.jersey.auth;

import java.nio.charset.StandardCharsets;

import com.google.common.base.Joiner;
import com.google.common.io.BaseEncoding;

import lombok.Value;

@Value
public class RegistryBasicAuthValue implements RegistryAuthValue
{

  private final String username;
  private final String password;

  public static RegistryBasicAuthValue fromEncoded(String encoded)
  {

    String input = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);

    String[] parts = input.trim().split(":", 2);

    if (parts.length != 2)
    {
      return null;
    }
    return new RegistryBasicAuthValue(parts[0], parts[1]);

  }

  public static RegistryBasicAuthValue fromCredentials(String username, String password)
  {
    return new RegistryBasicAuthValue(username, password);
  }

  public String toEncodedValue()
  {
    return BaseEncoding.base64().encode(Joiner.on(':').join(username, password).getBytes(StandardCharsets.UTF_8));
  }

}
