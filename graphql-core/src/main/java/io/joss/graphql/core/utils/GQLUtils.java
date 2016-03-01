package io.joss.graphql.core.utils;

import java.util.List;
import java.util.stream.Collectors;

import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.value.DefaultValueVisitor;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLVariableRef;
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

  public static final GQLValue resolve(GQLArgument arg, GQLObjectValue input)
  {

    return arg.value().apply(new DefaultValueVisitor<GQLValue>() {

      @Override
      public GQLValue visitDefaultValue(GQLValue value)
      {
        return value;
      }

      @Override
      public GQLValue visitVarValue(GQLVariableRef value)
      {
        if (input != null)
        {
          return input.entry(value.name()).orElse(null);
        }
        return null;
      }

    });
  }

}
