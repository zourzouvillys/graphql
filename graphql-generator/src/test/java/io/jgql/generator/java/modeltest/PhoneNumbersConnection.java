package io.jgql.generator.java.modeltest;

import java.util.List;

import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(autoField = true)
public class PhoneNumbersConnection
{

  public int getTotalCount()
  {
    return 0;
  }

  public List<PhoneNumbersEdge> getEdges()
  {
    return null;
  }

}
