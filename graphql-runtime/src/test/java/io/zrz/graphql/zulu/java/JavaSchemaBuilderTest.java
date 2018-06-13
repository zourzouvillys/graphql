package io.zrz.graphql.zulu.java;

import org.junit.Test;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.zulu.annotations.GQLMixin;
import io.zrz.graphql.zulu.annotations.GQLOutputType;
import io.zrz.graphql.zulu.executable.ExecutableSchema;
import io.zrz.graphql.zulu.executable.ExecutableSchemaBuilder;

public class JavaSchemaBuilderTest {

  @GQLOutputType(autoscan = true, name = "BaseQueryRoot")
  public static class AbstractQueryRoot {

    public String hello() {
      return "hello";
    }

  }

  public static final class QueryRoot extends @GQLMixin(disclose = true) AbstractQueryRoot {

    @Override
    public String hello() {
      return "hello";
    }

    public AnotherType more() {
      return null;
    }

  }

  public static final class AnotherType {

    public int count() {
      return 0;
    }

  }

  @Test
  public void test() {

    ExecutableSchema schema = new ExecutableSchemaBuilder()
        .addType(AnotherType.class, "SomeName")
        .addScalar(String.class)
        .setRootType(GQLOpType.Query, QueryRoot.class)
        .build();

  }

}
