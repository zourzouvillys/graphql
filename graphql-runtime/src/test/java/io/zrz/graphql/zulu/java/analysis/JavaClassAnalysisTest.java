package io.zrz.graphql.zulu.java.analysis;

import static io.zrz.graphql.zulu.binding.JavaBindingClassAnalysis.lookup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import io.zrz.graphql.zulu.User;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLNull;
import io.zrz.graphql.zulu.annotations.GQLExtension;

public class JavaClassAnalysisTest {

  @Test
  public void test() {

    assertFalse(lookup(TestUser.class).isExtensionClass());
    assertEquals(0, lookup(TestUser.class).extensionFields().count());

    assertTrue(lookup(TestUserExtension.class).isExtensionClass());
    assertEquals(1, lookup(TestUserExtension.class).extensionFields().count());

    assertFalse(lookup(TestUserMethodExtension.class).isExtensionClass());
    assertEquals(1, lookup(TestUserMethodExtension.class).extensionFields().count());

    lookup(TestUserMethodExtension.class)
        .extensionFields()
        .map(m -> m.receiverType())
        .map(t -> t.getRawType())
        .forEach(System.err::println);

    lookup(TestUserMethodExtension.class)
        .extensionFields()
        .forEach(m -> {
          System.err.println();
          System.err.print(" --- ");
          System.err.println(m.fieldName());
          System.err.println();
          System.err.println(m.typeParameters());
          System.err.println();
          System.err.println(m.annotations());
          System.err.println("");
          System.err.println(m.returnType());
          System.err.println(m.returnTypeAnnotations());
          System.err.println("");
          m.parameters().stream().forEach(System.err::println);
        });

  }

  public static class TestUser {

  }

  public static class TestGeneric<R> {

    public String in(List<R> value) {
      return null;
    }

    public R hello() {
      return null;
    }

  }

  @GQLExtension
  public static class TestUserExtension {

    public static int testField() {
      return 2;
    }

  }

  public static class TestUserMethodExtension<R> {

    @GQLExtension
    @GQLField(value = "hello")
    public static <T extends TestUser & User> @GQLNull int testField(T receiver, int x, List<? super T> names) {
      return 1;
    }

  }

}
