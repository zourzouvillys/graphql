package io.jgql.generator.java.modeltest;

import java.util.List;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;
import lombok.Value;

@GQLType(autoField = true, description = "A test user")
public class TestUser implements TestNode
{

  public String getId()
  {
    return null;
  }

  @Value
  @GQLType(autoField = true)
  public static final class ScalarName
  {
    private String firstName;
    private String lastName;
  }

  @GQLField(description = "The languages spoken by this user")
  public @GQLNonNull List<@GQLNonNull String> getLanguagesSpoken()
  {
    return null;
  }

  @GQLField(description = "the user's name")
  public ScalarName getName()
  {
    return null;
  }

  public int getAge()
  {
    return 0;
  }

  public PhoneNumbersConnection getPhoneNumbers()
  {
    return null;
  }

}
