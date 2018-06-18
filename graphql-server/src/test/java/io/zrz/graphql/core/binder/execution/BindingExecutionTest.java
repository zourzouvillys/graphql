package io.zrz.graphql.core.binder.execution;

import org.junit.Ignore;
import org.junit.Test;

import io.zrz.graphql.core.binder.BasicExecutor;
import io.zrz.graphql.core.binder.JsonValueWriter;
import io.zrz.graphql.core.binder.TypeScanner;
import io.zrz.graphql.core.binder.execution.sqltest.TestSqlQueryRoot;
import io.zrz.graphql.core.binder.farmtest.FarmQueryRoot;
import io.zrz.graphql.core.binder.testmodel.TestQueryRoot;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.lang.GQLSchemaBuilder;
import io.zrz.graphql.core.lang.GQLTypeRegistry;
import io.zrz.graphql.core.types.GQLTypes;
import io.zrz.graphql.core.utils.TypePrinter;
import io.zrz.graphql.core.value.GQLValue;

public class BindingExecutionTest
{

  @Test
  public void test()
  {

    // execute a query.
    GQLValue value = BasicExecutor.simpleQuery(
        new TestQueryRoot(),
        "{ age(b:1, c:[true], e:['a']), aliases1 { stage1: somethingElse(count:10) { id, someArray, stage2: somethingElse(count:12) { id } } } }");

    System.out.println(JsonValueWriter.toJSONString(value));

  }

  @Test
  public void testSQL()
  {

    // execute a query.
    GQLValue value = BasicExecutor.simpleQuery(
        new TestSqlQueryRoot("acme"),
        "{ tenant { a: employees { id, manager { id } }, b: employees { id, name, manager { name, age(age: \"123\"), manager { manager { id } } } } } } }");

    System.out.println(JsonValueWriter.toJSONString(value));

  }

  @Test
  public void testFarm()
  {

    GQLSchemaBuilder builder = new GQLSchemaBuilder();

    builder.add(GQLTypes.builtins());

    TypeScanner scanner = new TypeScanner(builder);

    GQLTypeDeclaration root = scanner.add(FarmQueryRoot.class);

    scanner.finish();

    GQLTypeRegistry reg = builder.build();

    reg.types().forEach(type -> {
      type.apply(new TypePrinter(System.out));
    });

    // execute a query.
    GQLValue value = BasicExecutor.simpleQuery(
        new FarmQueryRoot(),
        "{ counter, animals { name(plural: false), sound }}");

    System.out.println(JsonValueWriter.toJSONString(value));

  }

  @Ignore
  @Test
  public void testSpread()
  {

    // execute a query.
    GQLValue value = BasicExecutor.simpleQuery(
        new FarmQueryRoot(),
        "query withFragments { ... farmFields ... on MyClass { moo } } fragment farmFields on FarmQueryRoot { animals { sound } }");

    System.out.println(JsonValueWriter.toJSONString(value));

  }

}