package io.zrz.generator.java.modeltest;

import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.core.binder.annotatons.GQLType;

@GQLType(name = "Node", autoField = true)
public interface TestNode
{

  @GQLNonNull
  String getId();

}
