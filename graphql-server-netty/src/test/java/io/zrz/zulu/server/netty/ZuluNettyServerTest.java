package io.zrz.zulu.server.netty;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import io.zrz.graphql.zulu.annotations.GQLContext;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLOutputExtension;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableType;
import io.zrz.graphql.zulu.schema.GQLSchema;
import io.zrz.graphql.zulu.schema.GQLSchemaType;

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

    public GQLSchema __schema(@GQLContext ExecutableSchema schema) {
      return new GQLSchema(schema);
    }

    /**
     * 
     * @param schema
     * @param typeName
     * @return
     */

    public GQLSchemaType __type(@GQLContext ExecutableSchema schema, @GQLField("name") String typeName) {
      ExecutableType type = schema.resolveType(typeName);
      if (type == null) {
        return null;
      }
      return new GQLSchemaType(type);
    }

    /**
     * 
     * @param instance
     * @return
     */

    @GQLOutputExtension
    public static String __typename(Object instance) {
      return "test: " + instance.getClass().getName();
    }

  }

  @Test
  public void test() throws TimeoutException {

    System.err.println("starting up ...");

    // create engine
    ZuluEngine engine = ZuluEngine.builder()
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
