package io.jgql.generator.java;

import io.jgql.generator.java.MyTest.ListUsersAndPhoneNumbersResult;
import io.jgql.generator.java.MyTest.ListUsersAndPhoneNumbersResult.TestUser;
import io.joss.graphql.client.runtime.GQLClientBinder;

public class MyConsumer
{

  public static void main(String[] args)
  {

    MyTest.ClientStub client = GQLClientBinder.bind(MyTest.ClientStub.class, "https://localhost/gql");

    ListUsersAndPhoneNumbersResult res = client.listUsersAndPhoneNumbers("th*", "e164").execute();

    for (TestUser user : res.users())
    {
      user.phoneNumbers().forEach(number -> System.out.println(number.dialstring()));
    }

  }

}
