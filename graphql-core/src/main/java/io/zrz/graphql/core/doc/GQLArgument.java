package io.zrz.graphql.core.doc;

import org.immutables.value.Value;

import io.zrz.graphql.core.parser.GQLSourceRange;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValues;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLArgument {

  public abstract GQLSourceRange location();

  public abstract String name();

  public abstract GQLValue value();

  public static GQLArgument falseArg(final String name) {
    return builder().name(name).value(GQLValues.booleanFalse()).build();
  }

  public static GQLArgument trueArg(final String name) {
    return builder().name(name).value(GQLValues.booleanTrue()).build();
  }

  public static GQLArgument intArg(final String name, final long value) {
    return builder().name(name).value(GQLValues.intValue(value)).build();
  }

  public static GQLArgument stringArg(final String name, final String value) {
    return builder().name(name).value(GQLValues.stringValue(value)).build();
  }

  public static GQLArgument varArg(final String name, final String varname) {
    return builder().name(name).value(GQLValues.variable(varname)).build();
  }

  public static ImmutableGQLArgument.Builder builder() {
    return ImmutableGQLArgument.builder();
  }

}
