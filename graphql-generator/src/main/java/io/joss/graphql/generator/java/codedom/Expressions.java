package io.joss.graphql.generator.java.codedom;

public class Expressions
{

  public static FieldExpression thisField(String name)
  {
    return FieldExpression.builder().expression(ThisExpression.instance()).name(name).build();
  }

  public static SimpleNameExpression simpleName(String name)
  {
    return SimpleNameExpression.builder().name(name).build();
  }

}
