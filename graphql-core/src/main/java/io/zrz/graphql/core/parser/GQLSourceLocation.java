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

    return String.format("%s (line %d col %d)", this.input(), this.lineNumber(), this.lineOffset());

  }

}
