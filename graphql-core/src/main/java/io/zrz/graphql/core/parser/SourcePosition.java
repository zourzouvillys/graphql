package io.zrz.graphql.core.parser;

public class SourcePosition {

  private final int start;
  private final int end;

  public SourcePosition(final int start, final int end) {
    if (start > end) {
      throw new IllegalArgumentException();
    }
    this.start = start;
    this.end = end;
  }

  public int start() {
    return this.start;
  }

  public int end() {
    return this.end;
  }

  @Override
  public String toString() {
    return String.format("%s-%s", start(), end());
  }

  public static SourcePosition range(int start, int end) {
    return new SourcePosition(start, end);
  }

  @Override
  public int hashCode() {
    return start * 31 + end;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SourcePosition)) {
      return false;
    }
    if (((SourcePosition) o).start != this.start) {
      return false;
    }
    if (((SourcePosition) o).end != this.end) {
      return false;
    }
    return true;
  }

}
