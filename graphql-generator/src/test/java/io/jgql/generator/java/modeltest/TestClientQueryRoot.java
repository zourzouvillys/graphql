package io.jgql.generator.java.modeltest;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(autoField = true)
public class TestClientQueryRoot
{

  @GQLField(name = "node")
  public TestNode[] getNode(@GQLArg("id") String[] id)
  {
    return null;
  }

  public TestUsersConnection getUsers(@GQLArg("q") String query)
  {
    return null;
  }

  public TestUser getUser(@GQLArg("username") String username)
  {
    return null;
  }

}
