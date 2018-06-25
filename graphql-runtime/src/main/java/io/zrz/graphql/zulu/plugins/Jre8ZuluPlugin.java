package io.zrz.graphql.zulu.plugins;

import java.time.Instant;

import io.zrz.graphql.zulu.annotations.GQLExtension;
import io.zrz.graphql.zulu.engine.ZuluEngineBuilder;

/**
 * provides out of the box support for JRE 8 types.
 *
 * @author theo
 *
 */

public class Jre8ZuluPlugin implements ZuluPlugin {

  public static class ZuluInstant {

    @GQLExtension
    public static String isoValue(final Instant instant) {
      return instant.toString();
    }

  }

  @Override
  public void onPluginRegistered(final ZuluEngineBuilder builder) {

    builder.stubType(Instant.class);

    builder.extension(ZuluInstant.class);

  }

}
