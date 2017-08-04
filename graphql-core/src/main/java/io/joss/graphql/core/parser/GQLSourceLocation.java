package io.joss.graphql.core.parser;

import lombok.Value;

@Value(staticConstructor = "of")
public class GQLSourceLocation {

  private final GQLSourceInput input;

  private final int sourceOffset;
  private final int lineNumber;
  private final int lineOffset;

}
