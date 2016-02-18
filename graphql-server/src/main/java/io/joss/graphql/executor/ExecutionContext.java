package io.joss.graphql.executor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_MULTIPLYPeer;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLFieldSelection;
import io.joss.graphql.core.doc.GQLFragmentSpreadSelection;
import io.joss.graphql.core.doc.GQLInlineFragmentSelection;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.doc.GQLSelection;
import io.joss.graphql.core.doc.GQLSelectionVisitor;
import io.joss.graphql.core.parser.GQLException;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLObjectValue.Builder;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;
import io.joss.graphql.executor.GraphQLOutputType.Field;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutionContext
{

  private QueryEnvironment env;
  private GraphQLEngine engine;

  public ExecutionContext(GraphQLEngine engine, QueryEnvironment env, GQLSelectedOperation op)
  {
    this.engine = engine;
    this.env = env;
  }

  public QueryEnvironment env()
  {
    return env;
  }

  /**
   * Performs a selection.
   */

  public GQLObjectValue[] selections(GraphQLOutputType type, Object[] roots, List<GQLSelection> selections)
  {

    Preconditions.checkNotNull(type);

    GQLObjectValue.Builder[] res = new GQLObjectValue.Builder[roots.length];

    selections.forEach(s -> selection(type, roots, res, s));

    GQLObjectValue[] ret = new GQLObjectValue[roots.length];

    for (int i = 0; i < ret.length; ++i)
    {
      ret[i] = res[i].build();
    }

    return ret;
  }

  /**
   * @param res
   * 
   */

  private void selection(GraphQLOutputType type, Object[] roots, GQLObjectValue.Builder[] res, GQLSelection selection)
  {

    Preconditions.checkNotNull(type);

    selection.apply(new GQLSelectionVisitor<Void>() {

      @Override
      public Void visitFieldSelection(GQLFieldSelection selection)
      {

        Field field = type.field(selection.name());

        if (field == null)
        {
          throw new GQLException(String.format("Couldn't find field '%s'", selection.name()));
        }

        select(res, type, roots, field, selection);

        return null;
      }

      @Override
      public Void visitFragmentSelection(GQLFragmentSpreadSelection selection)
      {
        throw new RuntimeException();
      }

      @Override
      public Void visitInlineFragment(GQLInlineFragmentSelection selection)
      {
        throw new RuntimeException();
      }

    });

  }

  private void select(GQLObjectValue.Builder[] target, GraphQLOutputType type, Object[] roots, Field field, GQLFieldSelection selection)
  {

    // where we place our values.
    GQLValue[] result = new GQLValue[roots.length];

    // perform the query.
    query(result, type, roots, field, selection.args(), selection.selections());

    // for each value, append to the results.

    String alias = alias(selection);

    for (int i = 0; i < target.length; ++i)
    {
      if (result[i] != null)
      {
        if (target[i] == null)
        {
          target[i] = GQLObjectValue.builder();
        }
        target[i].value(alias, result[i]);
      }
    }

  }

  /**
   * Perform the given query.
   * 
   * @param results
   * @param type
   * @param roots
   * @param field
   * @param args
   * @param children
   */

  private void query(GQLValue[] results, GraphQLOutputType type, Object[] roots, Field field, List<GQLArgument> args, List<GQLSelection> children)
  {

    if (field.handler() == null)
    {
      // it's not an error to have no handler (unless the return type is @GQLNonNull), but that
      // is caught at creation time.
      return;
    }

    // we perform the request, which returns the java object.
    // if it's a scalar type (or list of), then convert and return. otherwise recurse.

    Object[] placeholder = field.handler().value(roots, type, field, this, args, children);

    Class<?> providedType = placeholder.getClass().getComponentType();

    if (providedType.isArray())
    {

      // the return is an array, so we're exploding.

      Object[][] deep = (Object[][]) placeholder;

      int count = 0;

      for (int i = 0; i < results.length; ++i)
      {
        count += deep[i].length;
      }

      Object[] deeper = (Object[]) Array.newInstance(providedType.getComponentType(), count);

      count = 0;

      // now, copy the elements in.
      for (int i = 0; i < results.length; ++i)
      {
        for (int x = 0; x < deep[i].length; ++x)
        {
          deeper[count++] = deep[i][x];
        }
      }

      Preconditions.checkState(count == deeper.length);

      // now apply the selection to it.

      GraphQLOutputType childType = this.engine.type(providedType.getComponentType());

      if (childType == null)
      {
        log.warn("Unable to find child type {}", providedType.getComponentType());
        // WARN: don't know how to convert the child type.
        // this only happens here rather than compile time so we can allow dynamic return types.
        return;
      }

      GQLObjectValue[] xxx = selections(childType, deeper, children);

      // now map the results back.

      count = 0;

      // now, copy the elements in.
      for (int i = 0; i < results.length; ++i)
      {

        GQLValue[] re = new GQLValue[deep[i].length];

        for (int x = 0; x < deep[i].length; ++x)
        {

          Object val = xxx[count++];

          if (val == null)
          {
            continue;
          }

          re[x] = (GQLValue) val;

        }

        results[i] = GQLValues.listValue(re);

      }

    }
    else if (!children.isEmpty())
    {

      // it's a sub-selection. we need to work out the handler to use.

      GraphQLOutputType childType = this.engine.type(providedType);

      if (childType == null)
      {
        log.warn("Unable to find child type {}", providedType);
        // WARN: don't know how to convert the child type.
        // this only happens here rather than compile time so we can allow dynamic return types.
        return;
      }

      GQLObjectValue[] xxx = selections(childType, placeholder, children);

      for (int i = 0; i < xxx.length; ++i)
      {
        results[i] = xxx[i];
      }

      return;

    }
    else
    {

      // now, convert from providedType to our expected return type.

      for (int i = 0; i < results.length; ++i)
      {

        // the java value needs to be converted into GQL.
        Object returnedValue = placeholder[i];

        // convert.

        results[i] = GQLValues.stringValue((returnedValue) != null ? returnedValue.toString() : "");

      }

    }

  }

  private String alias(GQLFieldSelection selection)
  {
    return selection.alias() == null ? selection.name() : selection.alias();
  }

}
