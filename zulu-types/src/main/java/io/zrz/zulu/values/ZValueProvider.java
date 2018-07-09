package io.zrz.zulu.values;

import io.zrz.zulu.types.ZType;
import io.zrz.zulu.types.ZTypeUse;

/**
 * supplier of late bound {@link ZValue} instances, with a {@link ZType} known in advance.
 */

public interface ZValueProvider {

  /**
   * the type that will be resolved.
   */

  ZTypeUse type();

  /**
   * resolves the value.
   * 
   * @param value
   * @return
   */

  ZValue resolve();

}
