package io.joss.graphql.core.utils;

import java.util.List;
import java.util.stream.Collectors;

import io.joss.graphql.core.doc.GQLArgument;
import lombok.experimental.UtilityClass;

@UtilityClass

public class GQLUtils
{

  /**
   * Pretty print the args.
   * 
   * @param args
   * @return
   */

  public static String toString(final List<GQLArgument> args)
  {
    return args.stream().map(arg -> toString(arg)).collect(Collectors.joining(", "));
  }

  private static String toString(final GQLArgument arg)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(arg.name());
    sb.append('=');
    sb.append(arg.value());
    return sb.toString();
  }

  public static boolean isValidTypeName(String name)
  {
    if (name == null || name.isEmpty())
    {
      return false;
    }
    return true;
  }

  public static String normalize(String input)
  {
    if (input == null)
    {
      return null;
    }
    input = input.trim();
    if (input.isEmpty())
    {
      return null;
    }
    return input;
  }

}
