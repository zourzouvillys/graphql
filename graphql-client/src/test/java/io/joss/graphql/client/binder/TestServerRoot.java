package io.joss.graphql.client.binder;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.collect.Lists;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType
public class TestServerRoot
{

  @GQLType
  public static class ServerTestPhoneNumber
  {

    @GQLField
    public String getId()
    {
      return  RandomStringUtils.randomAlphabetic(12);
    }

    @GQLField
    public String getName()
    {
      return "my PSTN number";
    }

    @GQLField
    public String getCallerName()
    {
      return "The Caller";
    }

    @GQLField
    public String getDialstring()
    {
      return RandomStringUtils.randomAlphabetic(7);
    }

  }

  @GQLType
  public static class ServerTestPhoneNumberEdge
  {

    @GQLField
    public ServerTestPhoneNumber getNode()
    {
      return new ServerTestPhoneNumber();
    }

  }

  @GQLType
  public static class ServerTestPhoneNumberConneciton
  {

    @GQLField

    public int getTotalCount()
    {
      return 3;
    }

    @GQLField
    public List<ServerTestPhoneNumberEdge> getEdges()
    {
      return Lists.newArrayList(
          new ServerTestPhoneNumberEdge(),
          new ServerTestPhoneNumberEdge(),
          new ServerTestPhoneNumberEdge());
    }

  }

  @GQLType
  public static class ServerTestName
  {
    private String firstName;
    private String lastName;

    public ServerTestName(String firstName, String lastName)
    {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    @GQLField
    public String getFirstName()
    {
      return this.firstName;
    }

    @GQLField
    public String getLastName()
    {
      return this.lastName;
    }
  }

  @GQLType
  public static class ServerTestUser
  {

    @GQLField
    public String getId()
    {
      return "xxx";
    }

    @GQLField
    public int getAge()
    {
      return 32;
    }

    @GQLField
    public ServerTestPhoneNumberConneciton getPhoneNumbers()
    {
      return new ServerTestPhoneNumberConneciton();
    }

    @GQLField
    public List<String> getLanguagesSpoken()
    {
      return Lists.newArrayList("English", "American", "Redneck", "Greek");
    }

    @GQLField
    public ServerTestName getName()
    {
      return new ServerTestName("theo", "zourzouvillys");
    }

  }

  @GQLField
  public ServerTestUser getUser()
  {
    return new ServerTestUser();
  }

}
