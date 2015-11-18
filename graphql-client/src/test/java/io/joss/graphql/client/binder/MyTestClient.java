package io.joss.graphql.client.binder;

import java.util.Collection;

import io.joss.graphql.core.binder.annotatons.GQLNonNull;

@javax.annotation.Generated(value = "io.joss.graphql.generator.java.JavaClientGenerator", date = "2015-11-18T14:16:31.503Z")
public class MyTestClient
{

  @lombok.ToString
  @lombok.EqualsAndHashCode
  public static class ListUsersAndPhoneNumbersResult
  {

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class ScalarName
    {

      private final String firstName;

      public String firstName()
      {
        return this.firstName;
      }

      private final String lastName;

      public String lastName()
      {
        return this.lastName;
      }

      @java.beans.ConstructorProperties({ "firstName", "lastName" })
      public ScalarName(String firstName, String lastName)
      {
        this.firstName = firstName;
        this.lastName = lastName;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class PhoneNumber
    {

      private final String id;

      public String id()
      {
        return this.id;
      }

      private final String name;

      public String name()
      {
        return this.name;
      }

      private final String dialstring;

      public String dialstring()
      {
        return this.dialstring;
      }

      private final String callerName;

      public String callerName()
      {
        return this.callerName;
      }

      @java.beans.ConstructorProperties({ "id", "name", "dialstring", "callerName" })
      public PhoneNumber(String id, String name, String dialstring, String callerName)
      {
        this.id = id;
        this.name = name;
        this.dialstring = dialstring;
        this.callerName = callerName;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class PhoneNumbersEdge
    {

      private final PhoneNumber node;

      public PhoneNumber node()
      {
        return this.node;
      }

      @java.beans.ConstructorProperties({ "node" })
      public PhoneNumbersEdge(PhoneNumber node)
      {
        this.node = node;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class PhoneNumbersConnection
    {

      private final int totalCount;

      public int totalCount()
      {
        return this.totalCount;
      }

      private final Collection<PhoneNumbersEdge> edges;

      public Collection<PhoneNumbersEdge> edges()
      {
        return this.edges;
      }

      @java.beans.ConstructorProperties({ "totalCount", "edges" })
      public PhoneNumbersConnection(int totalCount, Collection<PhoneNumbersEdge> edges)
      {
        this.totalCount = totalCount;
        this.edges = edges;
      }

    }

    /** A test user */
    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class TestUser
    {

      private final String id;

      public String id()
      {
        return this.id;
      }

      private final ScalarName name;

      /** the user's name */
      public ScalarName name()
      {
        return this.name;
      }

      private final int age;

      public int age()
      {
        return this.age;
      }

      private final PhoneNumbersConnection phoneNumbers;

      public PhoneNumbersConnection phoneNumbers()
      {
        return this.phoneNumbers;
      }

      private final @GQLNonNull Collection<@GQLNonNull String> languagesSpoken;

      /** The languages spoken by this user */
      public @GQLNonNull Collection<@GQLNonNull String> languagesSpoken()
      {
        return this.languagesSpoken;
      }

      @java.beans.ConstructorProperties({ "id", "name", "age", "phoneNumbers", "languagesSpoken" })
      public TestUser(String id, ScalarName name, int age, PhoneNumbersConnection phoneNumbers, @GQLNonNull Collection<@GQLNonNull String> languagesSpoken)
      {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.languagesSpoken = languagesSpoken;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class TestUserEdge
    {

      private final String cursor;

      public String cursor()
      {
        return this.cursor;
      }

      private final TestUser node;

      public TestUser node()
      {
        return this.node;
      }

      @java.beans.ConstructorProperties({ "cursor", "node" })
      public TestUserEdge(String cursor, TestUser node)
      {
        this.cursor = cursor;
        this.node = node;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class TestUsersConnection
    {

      private final Collection<TestUserEdge> edges;

      public Collection<TestUserEdge> edges()
      {
        return this.edges;
      }

      @java.beans.ConstructorProperties({ "edges" })
      public TestUsersConnection(Collection<TestUserEdge> edges)
      {
        this.edges = edges;
      }

    }

    private final TestUsersConnection users;

    public TestUsersConnection users()
    {
      return this.users;
    }

    @java.beans.ConstructorProperties({ "users" })
    public ListUsersAndPhoneNumbersResult(TestUsersConnection users)
    {
      this.users = users;
    }

  }

  @lombok.ToString
  @lombok.EqualsAndHashCode
  public static class GetUserResult
  {

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class ScalarName
    {

      private final String firstName;

      public String firstName()
      {
        return this.firstName;
      }

      private final String lastName;

      public String lastName()
      {
        return this.lastName;
      }

      @java.beans.ConstructorProperties({ "firstName", "lastName" })
      public ScalarName(String firstName, String lastName)
      {
        this.firstName = firstName;
        this.lastName = lastName;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class PhoneNumber
    {

      private final String id;

      public String id()
      {
        return this.id;
      }

      private final String name;

      public String name()
      {
        return this.name;
      }

      private final String dialstring;

      public String dialstring()
      {
        return this.dialstring;
      }

      private final String callerName;

      public String callerName()
      {
        return this.callerName;
      }

      @java.beans.ConstructorProperties({ "id", "name", "dialstring", "callerName" })
      public PhoneNumber(String id, String name, String dialstring, String callerName)
      {
        this.id = id;
        this.name = name;
        this.dialstring = dialstring;
        this.callerName = callerName;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class PhoneNumbersEdge
    {

      private final PhoneNumber node;

      public PhoneNumber node()
      {
        return this.node;
      }

      @java.beans.ConstructorProperties({ "node" })
      public PhoneNumbersEdge(PhoneNumber node)
      {
        this.node = node;
      }

    }

    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class PhoneNumbersConnection
    {

      private final int totalCount;

      public int totalCount()
      {
        return this.totalCount;
      }

      private final Collection<PhoneNumbersEdge> edges;

      public Collection<PhoneNumbersEdge> edges()
      {
        return this.edges;
      }

      @java.beans.ConstructorProperties({ "totalCount", "edges" })
      public PhoneNumbersConnection(int totalCount, Collection<PhoneNumbersEdge> edges)
      {
        this.totalCount = totalCount;
        this.edges = edges;
      }

    }

    /** A test user */
    @lombok.ToString
    @lombok.EqualsAndHashCode
    public static class TestUser
    {

      private final String id;

      public String id()
      {
        return this.id;
      }

      private final ScalarName name;

      /** the user's name */
      public ScalarName name()
      {
        return this.name;
      }

      private final int age;

      public int age()
      {
        return this.age;
      }

      private final PhoneNumbersConnection phoneNumbers;

      public PhoneNumbersConnection phoneNumbers()
      {
        return this.phoneNumbers;
      }

      private final @GQLNonNull Collection<@GQLNonNull String> languagesSpoken;

      /** The languages spoken by this user */
      public @GQLNonNull Collection<@GQLNonNull String> languagesSpoken()
      {
        return this.languagesSpoken;
      }

      @java.beans.ConstructorProperties({ "id", "name", "age", "phoneNumbers", "languagesSpoken" })
      public TestUser(String id, ScalarName name, int age, PhoneNumbersConnection phoneNumbers, @GQLNonNull Collection<@GQLNonNull String> languagesSpoken)
      {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phoneNumbers = phoneNumbers;
        this.languagesSpoken = languagesSpoken;
      }

    }

    private final TestUser user;

    public TestUser user()
    {
      return this.user;
    }

    @java.beans.ConstructorProperties({ "user" })
    public GetUserResult(TestUser user)
    {
      this.user = user;
    }

  }

  public interface ClientStub
  {

    io.joss.graphql.client.runtime.RuntimeQuery<ListUsersAndPhoneNumbersResult> listUsersAndPhoneNumbers(
        @io.joss.graphql.client.runtime.GQLParamName("userSearch") String userSearch, @io.joss.graphql.client.runtime.GQLParamName("numberFormat") String numberFormat);

    io.joss.graphql.client.runtime.RuntimeQuery<GetUserResult> getUser(@io.joss.graphql.client.runtime.GQLParamName("username") String username,
        @io.joss.graphql.client.runtime.GQLParamName("numberFormat") String numberFormat);

  }

}
