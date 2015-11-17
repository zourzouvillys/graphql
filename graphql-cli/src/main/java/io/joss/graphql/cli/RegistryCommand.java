package io.joss.graphql.cli;

import io.airlift.airline.Option;
import io.airlift.airline.OptionType;

public abstract class RegistryCommand implements Runnable
{

  @Option(type = OptionType.GLOBAL, name = "-v", description = "Verbose mode")
  public boolean verbose;

}
