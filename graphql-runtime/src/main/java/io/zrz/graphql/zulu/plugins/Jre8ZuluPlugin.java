package io.zrz.graphql.zulu.plugins;

import java.time.Instant;

import io.zrz.graphql.zulu.annotations.GQLOutputExtension;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;

/**
 * provides out of the box support for JRE 8 types.
 * 
 * @author theo
 *
 */

public class Jre8ZuluPlugin implements ZuluPlugin {

  public static class ZuluInstant {

    @GQLOutputExtension
    public static String isoValue(Instant instant) {
      return instant.toString();
    }

  }

  @Override
  public void onPluginRegistered(ZuluEngineBuilder builder) {

    builder.stubType(Instant.class);

    builder.type(ZuluInstant.class);

  }

}
