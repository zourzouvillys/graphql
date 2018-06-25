package io.zrz.graphql.zulu.engine;

import io.zrz.graphql.zulu.executable.ExecutableReceiverType;

/**
 * a container of selections. is both the root executable as well as and selections which themselves contain selections.
 *
 *
 *
 * @author theo
 *
 */

public interface ZuluSelectionContainer {

  /**
   * if this container will output a list.
   */

  boolean isList();

  /**
   * the executable this container is in.
   */

  ZuluExecutable executable();

  /**
   * the output type of this container.
   */

  ExecutableReceiverType outputType();

  /**
   * if this container is not the root node, then the name assigned to it.
   *
   * this will null for the root container.
   *
   */

  String outputName();

}
