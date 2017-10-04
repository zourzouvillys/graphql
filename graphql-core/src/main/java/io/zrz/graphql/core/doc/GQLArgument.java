package io.zrz.graphql.core.doc;

import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@Builder
@EqualsAndHashCode
@Wither
public class GQLArgument
{

  private final String name;
  private final GQLValue value;

  public String name()
  {
    return this.name;
  }

  public GQLValue value()
  {
    return this.value;
  }

  public static GQLArgument falseArg(final String name)
  {
    return builder().name(name).value(GQLValues.booleanFalse()).build();
  }

  public static GQLArgument trueArg(final String name)
  {
    return builder().name(name).value(GQLValues.booleanTrue()).build();
  }

  public static GQLArgument intArg(final String name, final long value)
  {
    return builder().name(name).value(GQLValues.intValue(value)).build();
  }

  public static GQLArgument stringArg(final String name, final String value)
  {
    return builder().name(name).value(GQLValues.stringValue(value)).build();
  }

  public static GQLArgument varArg(final String name, final String varname)
  {
    return builder().name(name).value(GQLValues.variable(varname)).build();
  }

}
