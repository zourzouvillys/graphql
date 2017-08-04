package io.joss.graphql.core.doc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder
public final class GQLDirective {

  private final String name;

  @Singular
  private final List<GQLArgument> args;

  public String name() {
    return this.name;
  }

  public List<GQLArgument> args() {
    return this.args;
  }

  public static GQLDirective createDirective(final String name, final GQLArgument... args) {
    return builder().name(name).args(Arrays.asList(args)).build();
  }

  public Optional<GQLArgument> arg(String string) {
    return this.args.stream().filter(c -> c.name().equals(string)).findAny();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("@").append(this.name);
    if (!this.args.isEmpty()) {
      sb.append("(");
      sb.append(this.args.stream().map(arg -> new StringBuilder().append(arg.name()).append(": ").append(arg.value())).collect(Collectors.joining(", ")));
      sb.append(")");
    }
    return sb.toString();

  }
}
