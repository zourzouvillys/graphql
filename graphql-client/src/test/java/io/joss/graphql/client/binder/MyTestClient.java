package io.joss.graphql.client.binder;

import java.util.Collection;

import io.joss.graphql.client.runtime.GQLPath;
import io.joss.graphql.client.runtime.RelayCollection;
import io.joss.graphql.client.runtime.RelayEdge;
import io.joss.graphql.client.runtime.RuntimeQuery;
import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;

class MyTestClient
{

  public interface ListUsersAndPhoneNumbersResult
  {

    @GQLPath("users.edges.node.name")
    public interface ScalarName
    {

      String firstName();

      String lastName();

    }

    @GQLPath("users.edges.node.phoneNumbers.edges.node")
    public interface PhoneNumber
    {

      String id();

      String name();

      String dialstring();

      String callerName();

    }

    @GQLPath("users.edges.node.phoneNumbers.edges")
    public interface PhoneNumbersEdge extends RelayEdge<PhoneNumbersConnection, PhoneNumbersEdge, PhoneNumber>
    {

      PhoneNumber node();

    }

    @GQLPath("users.edges.node.phoneNumbers")
    public interface PhoneNumbersConnection extends RelayCollection<PhoneNumbersConnection, PhoneNumbersEdge, PhoneNumber>
    {

      int totalCount();

      Collection<PhoneNumbersEdge> edges();

    }

    /** A test user */
    @GQLPath("users.edges.node")
    public interface TestUser
    {

      String id();

      /** the user's name */
      ScalarName name();

      int age();

      PhoneNumbersConnection phoneNumbers();

      /** The languages spoken by this user */
      @GQLNonNull
      Collection<@GQLNonNull String> languagesSpoken();

    }

    @GQLPath("users.edges")
    public interface TestUserEdge extends RelayEdge<TestUsersConnection, TestUserEdge, TestUser>
    {

      String cursor();

      TestUser node();

    }

    @GQLPath("users")
    public interface TestUsersConnection extends RelayCollection<TestUsersConnection, TestUserEdge, TestUser>
    {

      Collection<TestUserEdge> edges();

    }

    TestUsersConnection users();

  }

  public interface GetUserResult
  {

    @GQLPath("user.name")
    public interface ScalarName
    {

      String firstName();

      String lastName();

    }

    @GQLPath("user.phoneNumbers.edges.node")
    public interface PhoneNumber
    {

      String id();

      String name();

      String dialstring();

      String callerName();

    }

    @GQLPath("user.phoneNumbers.edges")
    public interface PhoneNumbersEdge extends RelayEdge<PhoneNumbersConnection, PhoneNumbersEdge, PhoneNumber>
    {

      PhoneNumber node();

    }

    @GQLPath("user.phoneNumbers")
    public interface PhoneNumbersConnection extends RelayCollection<PhoneNumbersConnection, PhoneNumbersEdge, PhoneNumber>
    {

      int totalCount();

      Collection<PhoneNumbersEdge> edges();

    }

    /** A test user */
    @GQLPath("user")
    public interface TestUser
    {

      String id();

      /** the user's name */
      ScalarName name();

      int age();

      PhoneNumbersConnection phoneNumbers();

      /** The languages spoken by this user */
      @GQLNonNull
      Collection<@GQLNonNull String> languagesSpoken();

    }

    TestUser user();

  }

  public interface ClientStub
  {

    RuntimeQuery<ListUsersAndPhoneNumbersResult> listUsersAndPhoneNumbers(@GQLArg("userSearch") String userSearch, @GQLArg("numberFormat") String numberFormat);

    RuntimeQuery<GetUserResult> getUser(@GQLArg("username") String username, @GQLArg("numberFormat") String numberFormat);

  }

}
