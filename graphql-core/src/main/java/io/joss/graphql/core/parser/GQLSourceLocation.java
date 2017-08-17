package io.joss.graphql.core.parser;

import lombok.Value;

@Value(staticConstructor = "of")
public class GQLSourceLocation {

  private final GQLSourceInput input;

  private final int sourceOffset;
  private final int lineNumber;
  private final int lineOffset;

  @Override
  public String toString() {

    return String.format("%s (line %d col %d)", this.input, this.lineNumber, this.lineOffset);

  }

}
