package io.zrz.graphql.core.parser;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
public abstract class GQLSourceLocation {

  public abstract GQLSourceInput input();

  public abstract int sourceOffset();

  public abstract int lineNumber();

  public abstract int lineOffset();

  @Override
  public String toString() {

    if (this.input() != null && this.input().name() != null && !this.input().name().isEmpty()) {
      return String.format("%s (line %d col %d)", this.input(), this.lineNumber(), this.lineOffset());
    }

    return String.format("(line %d col %d)", this.lineNumber(), this.lineOffset());

  }

}
