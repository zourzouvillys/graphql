package io.zrz.graphql.zulu.engine;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import io.zrz.graphql.zulu.annotations.GQLAutoScan;

public class ZuluEngineTest {

  @GQLAutoScan
  public static class TypeA {

    public String typeAfield() {
      return "result from calling TypeA";
    }

  }

  /**
   * 
   */

  public static class QueryRoot {

    public String hello(String name) {
      return "Hello, " + name + "!";
    }

    public int sum(int a, int b) {
      return a + b;
    }

    public QueryRoot child() {
      return this;
    }

    public String id() {
      return "frwf";
    }

    public int intcount() {
      return 0;
    }

    public long longcount() {
      return 1;
    }

    public TypeA other() {
      return new TypeA();
    }

  }

  private ZuluEngine engine;

  @Before
  public void setup() {
    this.engine = new ZuluEngine(QueryRoot.class);
  }

  @Test
  public void test() {

    // compile query, resulting in an executable.
    ZuluCompileResult result = engine.compile("{ "
        // + "a: hello(name: 'theo'),"
        // + "b: child { a: hello(name: 'alice'), b: hello(name: 'bob') }, "
        // + "c: sum(xa: 1, b: 2)"
        + "d: child { g: id, f: id, child { id, A: intcount, b: intcount, longcount } } "
        + "other { typeAfield } "
        + " }");

    if (!result.warnings().isEmpty()) {
      result.warnings().forEach(System.err::println);
    }

    ZuluExecutable executable = result.executable();

    System.err.println(" --- executing");

    // bind executable to a context.
    ZuluContext context = executable.bind(new QueryRoot());

    // now invoke a single field without parameters.
    Map<ZuluSelection, Object> values = context.execute();

    values.forEach((sel, val) -> System.err.println(sel.path() + "[" + sel.fieldType() + "]" + ": " + val));

    // System.err.println(context.execute(new DebugZuluResultReceiver(), "d"));

    // and then execute all fields in the operation at once.
    // context.execute()
    // .forEach((outputField, fieldValue) -> System.err.println(outputField + " = " + fieldValue));

  }

}
