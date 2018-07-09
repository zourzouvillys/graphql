package io.zrz.graphql.core.doc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLDirective {

  public abstract String name();

  public abstract List<GQLArgument> args();

  public static GQLDirective createDirective(final String name, final GQLArgument... args) {
    return builder().name(name).args(Arrays.asList(args)).build();
  }

  public Optional<GQLArgument> arg(final String string) {
    return this.args().stream().filter(c -> c.name().equals(string)).findAny();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("@").append(this.name());
    if (!this.args().isEmpty()) {
      sb.append("(");
      sb.append(this.args().stream().map(arg -> new StringBuilder().append(arg.name()).append(": ").append(arg.value())).collect(Collectors.joining(", ")));
      sb.append(")");
    }
    return sb.toString();

  }

  public static ImmutableGQLDirective.Builder builder() {
    return ImmutableGQLDirective.builder();
  }

}
