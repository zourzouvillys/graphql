package io.jgql.generator.java.modeltest;

import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "Node", autoField = true)
public interface TestNode
{

  @GQLNonNull
  String getId();

}
