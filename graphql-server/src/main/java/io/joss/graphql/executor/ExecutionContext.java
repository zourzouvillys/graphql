package io.joss.graphql.executor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.binder.execution.QueryEnvironment;
import io.joss.graphql.core.doc.GQLArgument;
import io.joss.graphql.core.doc.GQLFieldSelection;
import io.joss.graphql.core.doc.GQLFragmentDefinition;
import io.joss.graphql.core.doc.GQLFragmentSpreadSelection;
import io.joss.graphql.core.doc.GQLInlineFragmentSelection;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.doc.GQLSelection;
import io.joss.graphql.core.doc.GQLSelectionVisitor;
import io.joss.graphql.core.parser.GQLException;
import io.joss.graphql.core.value.DefaultValueVisitor;
import io.joss.graphql.core.value.GQLObjectValue;
import io.joss.graphql.core.value.GQLValue;
import io.joss.graphql.core.value.GQLValues;
import io.joss.graphql.core.value.GQLVariableRef;
import io.joss.graphql.executor.GraphQLOutputType.Field;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecutionContext
{

  private QueryEnvironment env;
  private GraphQLEngine engine;
  private GQLObjectValue input;
  private GQLSelectedOperation op;

  public ExecutionContext(GraphQLEngine engine, QueryEnvironment env, GQLSelectedOperation op)
  {
    this.engine = engine;
    this.env = env;
    this.op = op;
  }

  public QueryEnvironment env()
  {
    return env;
  }

  /**
   * Performs a selection on a set of root objects.
   */

  public GQLObjectValue[] selections(GraphQLOutputType type, Object[] roots, List<GQLSelection> selections)
  {

    Preconditions.checkNotNull(type);

    GQLObjectValue.Builder[] res = new GQLObjectValue.Builder[roots.length];

    selections.forEach(s -> selection(type, roots, res, s));

    GQLObjectValue[] ret = new GQLObjectValue[roots.length];

    for (int i = 0; i < ret.length; ++i)
    {
      if (res[i] != null)
      {
        ret[i] = res[i].build();
      }
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
          throw new GQLException(String.format("Couldn't find field '%s' on '%s'", selection.name(), type.name()));
        }

        select(res, type, roots, field, selection);

        return null;

      }

      @Override
      public Void visitFragmentSelection(GQLFragmentSpreadSelection selection)
      {

        // fetch the named fragment. if it doesn't exist it's an error.
        GQLFragmentDefinition fragment = op.doc().fragment(selection.name());

        if (fragment == null)
        {
          throw new GQLException(String.format("Unknown fragment '%s'", selection.name()));
        }

        if (!type.name().equals(fragment.namedType().name()))
        {
          // this type isn't the same as the one we're active on.
          log.debug("Skipping fragment '{}' on '{}'", fragment.namedType().name(), type.name());
          return null;
        }

        log.trace("Applying fragment spread {}", fragment);

        fragment.selections().forEach(s -> selection(type, roots, res, s));

        return null;

        // fragment.apply(this);
      }

      @Override
      public Void visitInlineFragment(GQLInlineFragmentSelection selection)
      {
        log.debug("Applying inline fragment selection with type condition {}", selection.typeCondition());
        throw new RuntimeException();
      }

    });

  }

  private void select(GQLObjectValue.Builder[] target, GraphQLOutputType type, Object[] roots, Field field, GQLFieldSelection selection)
  {

    // where we place our values.
    GQLValue[] result = new GQLValue[roots.length];

    // perform the query.
    query(result, type, roots, field, resolve(selection.args()), selection.selections());

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
        count += (deep[i] == null) ? 0 : deep[i].length;
      }

      Object[] deeper = (Object[]) Array.newInstance(providedType.getComponentType(), count);

      count = 0;

      // now, copy the elements in.
      for (int i = 0; i < results.length; ++i)
      {
        if (deep[i] != null)
        {
          for (int x = 0; x < deep[i].length; ++x)
          {
            deeper[count++] = deep[i][x];
          }
        }
      }

      Preconditions.checkState(count == deeper.length);

      // now apply the selection to it.

      GraphQLOutputType childType = this.engine.type(providedType.getComponentType());

      if (childType == null)
      {

        log.warn("Unable to find child type {} for component", providedType.getComponentType());
        engine.types().forEach(ktype -> log.debug(" -> {}", ktype.name()));
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

        if (deep[i] != null)
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

    }
    else if (providedType.isAssignableFrom(Collection.class))
    {

      // the return type is a collection.

      throw new RuntimeException("not yet supported");

    }
    else if (!children.isEmpty())
    {

      // it's a sub-selection. we need to work out the handler to use.

      GraphQLOutputType childType = this.engine.type(providedType);

      if (childType == null)
      {
        log.warn("Unable to find child type {} for non empty children", providedType);
        engine.types().forEach(ktype -> log.debug(" -> {}", ktype.name()));
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
        if (returnedValue != null)
        {
          results[i] = GQLValues.stringValue(returnedValue.toString());
        }

      }

    }

  }

  private String alias(GQLFieldSelection selection)
  {
    return selection.alias() == null ? selection.name() : selection.alias();
  }

  /**
   * sets the input for this execution.
   */

  public void input(GQLObjectValue input)
  {
    this.input = input;
  }

  /**
   * returns resolved arguments mapped against input.
   */

  private List<GQLArgument> resolve(List<GQLArgument> args)
  {
    return args.stream().map(this::resolve).collect(Collectors.toList());
  }

  private GQLArgument resolve(GQLArgument arg)
  {

    return arg.withValue(arg.value().apply(new DefaultValueVisitor<GQLValue>() {

      @Override
      public GQLValue visitDefaultValue(GQLValue value)
      {
        return value;
      }

      @Override
      public GQLValue visitVarValue(GQLVariableRef value)
      {
        if (input != null)
        {
          return input.entry(value.name()).orElse(null);
        }
        // TODO: should we treat as null, or fail? check spec.
        return null;
      }

    }));
  }

}
