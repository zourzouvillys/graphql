package io.jgql.core.binder.testmodel;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import io.joss.graphql.core.binder.annotatons.GQLArg;
import io.joss.graphql.core.binder.annotatons.GQLContext;
import io.joss.graphql.core.binder.annotatons.GQLField;
import io.joss.graphql.core.binder.annotatons.GQLNonNull;
import io.joss.graphql.core.binder.annotatons.GQLParent;
import io.joss.graphql.core.binder.annotatons.GQLType;
import io.joss.graphql.core.binder.runtime.DataContext;
import io.joss.graphql.core.binder.runtime.InputObserver;
import io.joss.graphql.core.binder.runtime.OutputObserver;

@GQLType
public class SomeNonInnerClass implements TestInterface
{

  private String id;

  public SomeNonInnerClass(String id)
  {
    this.id = id;
  }

  /**
   * We don't need a @GQLField on this method, as the interface includes it.
   */

  @Override
  @GQLField
  public String getId()
  {
    return id;
  }

  @GQLField

  public List<String> getSomeArray()
  {
    return Lists.newArrayList("a", "b", "c");
  }

  /**
   * this reader provides values asynchronously.
   * 
   * the interesting thing here is that we can generate a single SQL query along with the right fields to fetch based on the next query.
   * This means if we have 10 nodes each with 10 child nodes and each fetching another 10, we only perform 3 SQL queries as we recurse the
   * tree, rather than 1000 queries.
   * 
   * This usage of the query API also behaves asynchronously, allowing each implementation to decide when/how to load child queries.
   * 
   * @param output
   * @param selector
   * @return
   */

  @GQLNonNull
  @GQLField
  public static InputObserver<SomeNonInnerClass, SomeNonInnerClass> getSomethingElse(@GQLArg("count") int count, @GQLContext DataContext selector)
  {

    // we know the fields being selected based on DataContext.selector().

    return new InputObserver<SomeNonInnerClass, SomeNonInnerClass>() {

      @Override
      public void onParent(SomeNonInnerClass parent, OutputObserver<SomeNonInnerClass, SomeNonInnerClass> output)
      {

        for (int i = 0; i < count; ++i)
        {
          output.onNext(parent, new SomeNonInnerClass(String.format("somethingElse-%s-%s", parent.id, i)));
        }

        output.onComplete();

      }

      @Override
      public void onCompleted()
      {
        // no async so no problemo.
      }

    };

  }

  @GQLField
  public boolean isAwesome()
  {
    return true;
  }

  @GQLField
  public SomeNonInnerClass getSingleChild()
  {
    return this;
  }

  /**
   * 
   * @param first
   * @param ctx
   * @return
   */

  @GQLField
  @GQLNonNull
  public static Collection<@GQLNonNull AnotherObject> getAnother(
      @GQLContext @GQLParent List<SomeNonInnerClass> parent,
      @GQLContext DataContext ctx)
  {

    // for (DataContext child : ctx.children())
    // {
    // System.err.println(child.path());
    // }

    return Lists.newArrayList();

  }

}
