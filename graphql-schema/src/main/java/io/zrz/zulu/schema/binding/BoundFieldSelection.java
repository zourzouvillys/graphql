package io.zrz.zulu.schema.binding;

/**
 * a selection which it itself a field on a parent selection.
 * 
 * @author theo
 *
 */

public interface BoundFieldSelection extends BoundElement {

  /**
   * the output name of this field in the parent.
   */

  String outputName();

}
