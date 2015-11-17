package io.joss.graphql.generator.java.codedom;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class SingleVariableDeclaration
{

  private String name;
  private String type;
  private boolean varargs;
  private int modifiers;
  
}
