package io.zrz.graphql.generator;

import com.github.rvesse.airline.annotations.Cli;

@Cli(

    name = "gqlgen",
    defaultCommand = GenerateCommand.class,
    commands = {

    })

public class Main {

  public static void main(final String[] args) throws Throwable {
    final com.github.rvesse.airline.Cli<CliRunnable> cli = new com.github.rvesse.airline.Cli<>(Main.class);
    final CliRunnable cmd = cli.parse(args);
    cmd.run();
  }

}
