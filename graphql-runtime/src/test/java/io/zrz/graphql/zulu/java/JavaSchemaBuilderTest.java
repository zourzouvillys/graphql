package io.zrz.graphql.zulu.java;

import static org.junit.Assert.assertEquals;

import java.lang.invoke.MethodHandle;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.base.Stopwatch;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.zulu.annotations.GQLAutoScan;
import io.zrz.graphql.zulu.annotations.GQLInputType;
import io.zrz.graphql.zulu.annotations.GQLMixin;
import io.zrz.graphql.zulu.annotations.GQLOutputType;
import io.zrz.graphql.zulu.annotations.GQLSpread;
import io.zrz.graphql.zulu.executable.ExecutableInvoker;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder;
import io.zrz.graphql.zulu.schema.SchemaGenerator;

public class JavaSchemaBuilderTest {

  @GQLInputType
  public static class MoreArgs {

    public String value;

    public String xxx() {
      return null;
    }

  }

  @GQLAutoScan
  @GQLOutputType(name = "BaseQueryRoot")
  public static class AbstractQueryRoot {

    public String hsello() {
      return "hello";
    }

    public String ehrlow() {
      return "hello";
    }

    public String hello2() {
      return "hello";
    }

  }

  public static final class QueryRoot extends @GQLMixin(disclose = true) AbstractQueryRoot {

    private static final String HELLO = "hello there";
    private static final String[] HELLO_ARRAY = { "hello", "world" };
    private static final List<String> HELLO_LIST = Arrays.asList("hello1", "hello2");

    public String hello() {
      return HELLO;
    }

    public String[] hellosArray() {
      return HELLO_ARRAY;
    }

    public Optional<String[]> optionalHellosArray() {
      return Optional.of(HELLO_ARRAY);
    }

    public List<String> hellosList() {
      return HELLO_LIST;
    }

    public LinkedList<String> helloLinkedList() {
      return new LinkedList<String>();
    }

    public Iterable<String> helloIterable() {
      return HELLO_LIST;
    }

    public Optional<String> optionalHello() {
      return Optional.of("hello from helloOptional");
    }

    public CompletableFuture<AnotherType> asyncMore(MoreArgs args) {
      return null;
    }

    public CompletableFuture<Optional<AnotherType>> asyncNullableMore(MoreArgs args) {
      return null;
    }

    public Optional<Optional<AnotherType>> asyncOptionalNullableMore(MoreArgs args) {
      return null;
    }

    public Optional<AnotherType> more(@GQLSpread MoreArgs args) {
      return null;
    }

    public Optional<AnotherType> singleInput(String str) {
      return null;
    }

    public Optional<AnotherType> primitiveInput(int intval) {
      return null;
    }

    public int primitiveInputOutput(int intval) {
      return 1;
    }

    public int mixedInOut(Optional<String> optval, int pint1, int[] pint2, Integer int1, String xyz, boolean bool) {
      return 1;
    }

  }

  public static final class AnotherType {

    public int count() {
      return 0;
    }

  }

  @Test
  public void testInputParameters() throws Throwable {

    ExecutableSchema schema = new ExecutableSchemaBuilder()
        .setRootType(GQLOpType.Query, QueryRoot.class)
        .addType(AnotherType.class)
        .build(false);

    ExecutableInvoker handle = schema
        .resolve("QueryRoot", "mixedInOut")
        .invoker();

    System.err.println(new SchemaGenerator(schema).generate());

    System.err.println("---");

    handle.parameters()
        .get()
        .fields()
        .values()
        .stream()
        .forEach(System.err::println);

  }

  @Test
  public void test() throws Throwable {

    ExecutableSchema schema = new ExecutableSchemaBuilder()
        .setRootType(GQLOpType.Query, QueryRoot.class)
        .addType(AnotherType.class)
        .build(false);

    // System.err.println(new SchemaGenerator(schema).generate());

    ExecutableInvoker handle = schema
        .resolve("QueryRoot", "helloLinkedList")
        .invoker();

    System.err.println(handle.parameters());

    assertEquals(1, handle.arity());

    MethodHandle invoker = handle.methodHandle();

    String[] out = (String[]) invoker.invokeExact(new QueryRoot());

    QueryRoot root = new QueryRoot();

    Stopwatch timer = Stopwatch.createStarted();

    int loops = 1_000_000;

    for (int i = 0; i < loops; i++) {

      String[] res = (String[]) invoker.invokeExact(root);

      // if (res.length() == 0) {
      // throw new IllegalAccessError();
      // }

    }

    // timer per call...
    System.err.println(timer.stop().elapsed(TimeUnit.NANOSECONDS) / loops);

  }

}
