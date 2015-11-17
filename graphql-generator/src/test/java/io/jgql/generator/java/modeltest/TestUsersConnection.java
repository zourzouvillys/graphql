package io.jgql.generator.java.modeltest;

import java.util.List;

import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(autoField = true)
public class TestUsersConnection
{

  public List<TestUserEdge> getEdges()
  {
    return null;
  }

}
