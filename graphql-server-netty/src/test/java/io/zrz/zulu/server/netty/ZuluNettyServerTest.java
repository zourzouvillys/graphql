package io.zrz.zulu.server.netty;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import io.zrz.graphql.plugins.jackson.ZuluJacksonPlugin;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.plugins.Jre8ZuluPlugin;
import io.zrz.graphql.zulu.schema.GQLSchema;

public class ZuluNettyServerTest {

  @GQLDocumentation("A Test Class")
  public static class TestHelloService {

    public String hello(String name) {
      return "Hello, " + name;
    }

  }

  @GQLDocumentation("A Test Class")
  public static class TestQueryRoot {

    public String hello() {
      return "hello, there!";
    }

    public int count() {
      return 1;
    }

    public List<String> listing() {
      return null;
    }

    public Integer maybecount() {
      return null;
    }

    public Instant now() {
      return Instant.now();
    }

    @GQLDocumentation("test a flag")
    public boolean flag(@GQLField("test") String test) {
      return true;
    }

    public TestQueryRoot self() {
      return this;
    }

    public MyOther other() {
      return new MyOther();
    }

  }

  /**
   * 
   */

  @GQLObjectType
  public static class MyOther {

    public String another() {
      return "bnoom";
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
        .plugin(new Jre8ZuluPlugin())
        .build();

    // start up.
    ZuluNettyServer.create(9999, engine)
        .startAsync()
        .awaitTerminated(15, TimeUnit.SECONDS);

  }

}
