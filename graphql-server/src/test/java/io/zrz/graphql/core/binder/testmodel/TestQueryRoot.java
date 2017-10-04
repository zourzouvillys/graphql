package io.zrz.graphql.core.binder.testmodel;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import io.zrz.graphql.core.binder.annotatons.GQLArg;
import io.zrz.graphql.core.binder.annotatons.GQLBeanParams;
import io.zrz.graphql.core.binder.annotatons.GQLContext;
import io.zrz.graphql.core.binder.annotatons.GQLDefaultValue;
import io.zrz.graphql.core.binder.annotatons.GQLField;
import io.zrz.graphql.core.binder.annotatons.GQLNonNull;
import io.zrz.graphql.core.binder.annotatons.GQLType;
import io.zrz.graphql.core.binder.annotatons.GQLTypeUse;
import io.zrz.graphql.core.binder.runtime.DataContext;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@GQLType
public class TestQueryRoot
{

  @GQLType(type = GQLInputTypeDeclaration.class)
  public static class MyInputClass
  {

    @GQLField
    @GQLNonNull
    @GQLDefaultValue("cows")
    public String moo;

  }

  public static class BaseArgs
  {
    @GQLDefaultValue("hello")
    public String baseA;
  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  public static class MyArgs extends BaseArgs
  {

    public String a;

    @GQLDefaultValue("1")
    public int b;

    public List<@GQLNonNull Boolean>   c;

    // note: lombok destroys this one's return type (skips the component annotation).
    @Getter(onMethod = @__({ @GQLNonNull }) )
    private @GQLNonNull List<@GQLNonNull String> e;

  }

  @GQLField
  public @GQLNonNull @GQLTypeUse("ID") String id()
  {
    return "xyz";
  }

  @GQLField
  public int age(@GQLBeanParams MyArgs args)
  {
    return 33;
  }

  @GQLField
  public SomeNonInnerClass getSomething()
  {
    return new SomeNonInnerClass("somethingRoot");
  }

  @GQLField
  public String something(@GQLArg("$input") MyInputClass input)
  {
    return "xyz";
  }

  @GQLField
  public String somethingNull(@GQLArg("$input") MyInputClass input)
  {
    return null;
  }

  @GQLField
  public int requiredArg(@GQLArg("val") @GQLDefaultValue("1234") int val)
  {
    return 1;
  }

  @GQLField(description = "Returns a simple string argument")
  public int stringArg(
      @GQLArg("strval") @GQLDefaultValue("SomeValue") @GQLNonNull String val,
      @GQLArg("xxx") @GQLDefaultValue("false") String xxx)
  {
    return 1;
  }

  @GQLField(description = "Returns a simple boolean argument")
  public boolean boolArg(
      @GQLArg("strval") @GQLDefaultValue("SomeValue") @GQLNonNull String val,
      @GQLArg("xxx") @GQLDefaultValue("false") boolean xxx)
  {
    return xxx;
  }

  /**
   * the static version.
   * 
   * @param first
   * @return
   */

  @GQLField
  @GQLNonNull
  public Collection<@GQLNonNull SomeNonInnerClass> getAliases1(@GQLArg("first") Integer first, @GQLContext DataContext ctx)
  {
    return Lists.newArrayList(
        new SomeNonInnerClass("A"),
        new SomeNonInnerClass("B"),
        new SomeNonInnerClass("C"),
        new SomeNonInnerClass("D"),
        new SomeNonInnerClass("E"),
        new SomeNonInnerClass("F"));
  }

  /**
   * the per-instance version.
   * 
   * @param first
   * @return
   */

  @GQLField
  @GQLNonNull
  public List<@GQLNonNull SomeNonInnerClass> getAliases2(@GQLArg("first") Integer first)
  {
    return Lists.newArrayList(
        new SomeNonInnerClass("A1"),
        new SomeNonInnerClass("B1"),
        new SomeNonInnerClass("C1"),
        new SomeNonInnerClass("D1"),
        new SomeNonInnerClass("E1"),
        new SomeNonInnerClass("F1"));
  }

}
