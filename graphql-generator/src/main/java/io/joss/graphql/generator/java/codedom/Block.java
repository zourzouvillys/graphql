package io.joss.graphql.generator.java.codedom;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class Block
{
  @Singular
  private List<Statement> statements;
}
