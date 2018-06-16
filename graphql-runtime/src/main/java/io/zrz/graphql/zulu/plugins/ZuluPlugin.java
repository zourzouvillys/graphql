package io.zrz.graphql.zulu.plugins;

import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;

/**
 * helper interface to make registering a bunch of features easier for the consumer.
 * 
 * @author theo
 *
 */
public interface ZuluPlugin {

  /**
   * called as soon as the plugin is registered against the builder.
   */

  default void onPluginRegistered(ZuluEngineBuilder builder) {

  }

  /**
   * called just before building the engine.
   */

  default void onBuilding(ZuluEngineBuilder builder) {

  }

  /**
   * called just after the engine has been built.
   */

  default void onEngine(ZuluEngine engine) {

  }

}
