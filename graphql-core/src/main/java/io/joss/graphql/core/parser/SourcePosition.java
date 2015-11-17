package io.joss.graphql.core.parser;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SourcePosition
{

  private final int start;
  private final int end;

  public SourcePosition(final int start, final int end)
  {
    if (start > end)
    {
      throw new IllegalArgumentException();
    }
    this.start = start;
    this.end = end;
  }

  public int start()
  {
    return this.start;
  }

  public int end()
  {
    return this.end;
  }

  public String toString()
  {
    return String.format("%s-%s", start(), end());
  }

  public static SourcePosition range(int start, int end)
  {
    return new SourcePosition(start, end);
  }

}
