package io.zrz.graphql.zulu.relay;

/**
 * an untyped reference to a node.
 *
 * @author theo
 *
 */

public class DynamicNodeRef {

  private final String typeName;
  private final String key;

  DynamicNodeRef(final String typeName, final String key) {
    this.typeName = typeName;
    this.key = key;
  }

  /**
   * the key for this type, which does not include the typename.
   */

  public String key() {
    return this.key;
  }

  /**
   * the typename identifier.
   */

  public String typeName() {
    return this.typeName;
  }

}
