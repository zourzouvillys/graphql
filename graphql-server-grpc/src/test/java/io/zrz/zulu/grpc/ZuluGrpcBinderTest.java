package io.zrz.zulu.grpc;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;
import io.reactivex.Flowable;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLObjectType;
import io.zrz.graphql.zulu.engine.ZuluEngine;
import io.zrz.graphql.zulu.plugins.Jre8ZuluPlugin;
import io.zrz.graphql.zulu.schema.GQLSchema;
import io.zrz.zulu.graphql.GraphQLGrpc;
import io.zrz.zulu.graphql.GraphQLProtos.PrepareReply.PreparedOperation;
import io.zrz.zulu.graphql.RxGraphQLGrpc;

public class ZuluGrpcBinderTest {

  @Test
  public void test() throws IOException, InterruptedException {

    // create engine
    ZuluEngine engine = ZuluEngine.builder()
        .type(GQLSchema.class)
        .queryRoot(TestQueryRoot.class)
        .schema(s -> s.allowedAutoloader(type -> false))
        .plugin(new Jre8ZuluPlugin())
        .build();

    Server server = InProcessServerBuilder.forName("zulu")
        .intercept(TransmitStatusRuntimeExceptionInterceptor.instance())
        .addTransportFilter(new BindingServerTransportFilter(() -> new ZuluGrpcBinder(engine)))
        .addService(new PerSessionService(GraphQLGrpc.getServiceDescriptor()))
        .build()
        .start();

    ZuluGrpcClient client = new ZuluGrpcClient(InProcessChannelBuilder.forName("zulu").build());

    Map<String, PreparedOperation> ops = client
        .prepare(
            "query myQuery ($a: String!, $b: Int!) { a: count, hello, count, maybecount, xxx, listing, structList { hello, listing } boo(test: $a, val: $b) }")
        .flatMapIterable(reply -> reply.getOperationsList())
        .toMap(x -> x.getOperationName())
        .blockingGet();

    Flowable.range(0, 100_000)
        .flatMap(seq -> {

          return client.execute(ops.get("myQuery").getOperationId(),
              Struct.newBuilder()
                  .putFields("a", Value.newBuilder().setStringValue("bob").build())
                  .putFields("b", Value.newBuilder().setNumberValue(123).build())
                  .build());

        })
        .blockingSubscribe(res -> {
        }, err -> System.err.println("ERROR: " + err.getMessage()));

    Stopwatch start = Stopwatch.createStarted();

    Flowable.range(0, 100_000)
        .flatMap(seq -> {

          return client.execute(ops.get("myQuery").getOperationId(),
              Struct.newBuilder()
                  .putFields("a", Value.newBuilder().setStringValue("bob").build())
                  .putFields("b", Value.newBuilder().setNumberValue(123).build())
                  .build());

        }, 128)
        .blockingSubscribe(res -> {
        }, err -> System.err.println("ERROR: " + err.getMessage()));

    start.stop();

    System.err.println(start.elapsed(TimeUnit.MICROSECONDS) / 100_000.0);

    // client.query("query ($a: String!, $b: Int!) { hello, count, maybecount, listing, structList { hello } boo(test:
    // $a, val: $b) }", Struct.newBuilder()
    // .putFields("a", Value.newBuilder().setStringValue("bob").build())
    // .putFields("b", Value.newBuilder().setNumberValue(123).build())
    // .build())
    // .doAfterTerminate(() -> server.shutdownNow())
    // .blockingSubscribe(System.err::println, err -> System.err.println("ERROR: " + err.getMessage()));

    server.shutdownNow();

    server.awaitTermination();

  }

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

    public List<String> nullListing() {
      return null;
    }

    public List<String> listing() {
      return Arrays.asList("a", "b", "c");
    }

    public List<TestQueryRoot> structList() {
      return Arrays.asList(this, this, this);
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

    public String boo(@GQLField("test") String test, @GQLField("val") int val) {
      return test + ": " + val;
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

}
