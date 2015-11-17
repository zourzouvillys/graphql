package io.joss.graphql.generator.java;

public class CodeUtils
{

  public static String toTypeName(String name)
  {
    return String.format("%s%s", Character.toUpperCase(name.charAt(0)), name.substring(1));    
  }

}
