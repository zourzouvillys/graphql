package io.zrz.graphql.zulu.java;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.Team;
import io.zrz.graphql.zulu.User;
import io.zrz.graphql.zulu.annotations.GQLOutputExtension;
import io.zrz.graphql.zulu.annotations.RelayAutoConnection;
import io.zrz.graphql.zulu.binding.JavaBindingProvider;

@SuppressWarnings("serial")
public class JavaClassContributorTest {

  @Test
  public void test() {

    JavaBindingProvider jc = new JavaBindingProvider();

    // register a type with it's name.
    jc.registerType(User.class);

    // register type, name will be derived automatically.
    jc.registerType(Team.class);

    jc.registerType(MyUser.class);

    // registers a concrete named type
    jc.registerType(new TypeToken<MyGenericType<User>>() {});

  }

  public static class MyGenericType<T> {

    public String hello() {
      return "bo";
    }

    @Override
    public String toString() {
      return getClass().toString();
    }

  }

  /**
   * 
   */

  public static class MyRef<T> {

    public MyRef(T source) {

    }

  }

  /**
   * a static extension field.
   */

  @GQLOutputExtension
  public static class MyUserExtension {

    public static String name(User user) {
      return "alice";
    }

    @RelayAutoConnection(cursorMethod = "id")
    public static List<MyUser> friends(User user, String name, int age) {
      return Collections.emptyList();
    }

  }

  /**
   * an instance based extension field.
   */

  public static class MyUserAgeField {

    private MyUser user;

    MyUserAgeField(MyUser user) {
      this.user = user;
    }

    public int get() {
      return 1;
    }

  }

  /**
   * the root type
   */

  public static class MyUser {

    public String hello() {
      return "";
    }

    public MyUser moo() {
      return null;
    }

  }

}
