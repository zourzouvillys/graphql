package io.zrz.graphql.generator;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.model.CommandGroupMetadata;
import com.github.rvesse.airline.model.CommandMetadata;
import com.github.rvesse.airline.model.GlobalMetadata;

@Command(hidden = true, name = "help")
public class GroupHelpCommand implements Runnable {

  @Inject
  private GlobalMetadata<?> globalMetadata;

  @Inject
  private CommandGroupMetadata groupMetadata;

  @Inject
  private CommandMetadata commandMetadata;

  @Override
  public void run() {

    final int len = IntStream.concat(commands().mapToInt(x -> x.getName().length()), subGroups().mapToInt(x -> x.getName().length()))
        .max()
        .orElse(0);

    if (commands().findAny().isPresent()) {
      System.out.println();
      System.out.println("commands:");
      System.out.println();
      commands()
          .sorted((a, b) -> a.getName().compareTo(b.getName()))
          .forEach(cmd -> {
            System.out.println("    " + StringUtils.rightPad(cmd.getName(), len) + "   " + cmd.getDescription());
          });
      System.out.println();
    }

    if (subGroups().findAny().isPresent()) {
      System.err.println();
      System.out.println("sub-groups:");
      System.out.println();
      subGroups()
          .sorted((a, b) -> a.getName().compareTo(b.getName()))
          .forEach(cmd -> {
            System.out.println("    " + StringUtils.rightPad(cmd.getName(), len) + "   " + cmd.getDescription());
          });
      System.out.println();
    }

  }

  private Stream<CommandMetadata> commands() {
    if (groupMetadata != null) {
      return groupMetadata.getCommands().stream().filter(x -> !x.isHidden());
    }
    return globalMetadata.getDefaultGroupCommands().stream();
  }

  private Stream<CommandGroupMetadata> subGroups() {
    if (groupMetadata != null) {
      return groupMetadata.getSubGroups().stream().filter(x -> !x.isHidden());
    }
    return globalMetadata.getCommandGroups().stream();
  }

}
