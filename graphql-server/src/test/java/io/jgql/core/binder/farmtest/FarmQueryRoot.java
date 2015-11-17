package io.jgql.core.binder.farmtest;

import java.util.List;

import com.google.common.collect.Lists;

import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLType;

@GQLType
public class FarmQueryRoot
{

  @GQLField
  public int getCounter()
  {
    return 1234;
  }

  @GQLField
  public @GQLNonNull List<@GQLNonNull Animal> getAnimals()
  {
    return Lists.newArrayList(
        new Animal("cow", "cows", "moo"),
        new Animal("pig", "pigs", "oink"),
        new Animal("sheep", "sheep", "baah"),
        new Animal("horse", "horses", "neigh"),
        new Animal("fox", "foxes", "Ring-ding-ding-ding-dingeringeding!"));
  }

}
