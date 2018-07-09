package io.zrz.zulu.types;

import java.util.Collections;
import java.util.List;

/**
 * a use of another type.
 */

public class ZTypeUse {

  private ZType type;
  private List<ZAnnotation> annotations = null;

  public ZTypeUse(ZType type) {
    this.type = type;
  }

  /**
   * the type that this use references.
   */

  public ZType type() {
    return this.type;
  }

  @Override
  public String toString() {
    return type.toString();
  }

  /**
   * the annotations defined on this type usage.
   */

  public List<ZAnnotation> annotations() {
    if (annotations == null)
      return Collections.emptyList();
    return this.annotations;
  }

  /**
   * create a direct usage of a type.
   */

  public static final ZTypeUse of(ZType type) {
    return new ZTypeUse(type);
  }

}
