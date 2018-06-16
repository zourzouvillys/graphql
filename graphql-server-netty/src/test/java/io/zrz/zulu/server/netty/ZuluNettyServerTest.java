package io.zrz.zulu.server.netty;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.schema.GQLSchema;

public class ZuluNettyServerTest {

  @GQLDocumentation("# A Test Class")
  public static class TestQueryRoot {

    public String hello() {
      return "hello, there!";
    }

    public int count() {
      return 1;
    }

    public Integer maybecount() {
      return null;
    }

    @GQLDocumentation("test a flag")
    public boolean flag(@GQLField("test") String test) {
      return true;
    }

    public TestQueryRoot self() {
      return this;
    }

  }

  @Test
  public void test() throws TimeoutException {

    System.err.println("starting up ...");

    // create engine
    ZuluEngine engine = ZuluEngine.builder()
        .type(GQLSchema.class)
        .queryRoot(TestQueryRoot.class)
        .schema(s -> s.allowedAutoloader(type -> false))
        .plugin(new ZuluJacksonPlugin())
        .build();

    // start up.
    ZuluNettyServer.create(engine)
        .startAsync()
        .awaitTerminated(15, TimeUnit.SECONDS);

  }

}
