package io.zrz.graphql.zulu.spi;

import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaOutputField;

/**
 * used to generate extensions dynamically, without code analysis.
 * 
 * @author theo
 *
 */

public interface ExtensionGenerator {

  /**
   * called when a new view type is created to generate extensions that should be applied to it.
   */

  Stream<JavaOutputField> generateExtensions(TypeToken<?> type);

}
