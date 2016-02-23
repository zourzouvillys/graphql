package io.joss.graphql.executor;

import com.google.common.base.Strings;

import io.joss.graphql.core.binder.annotatons.GQLInputType;
import io.joss.graphql.core.binder.annotatons.GQLType;

public final class ExecutorUtils
{

  public static String getGQLTypeName(Class<?> klass)
  {

    GQLType type = klass.getAnnotation(GQLType.class);

    if (type != null)
    {
      if (Strings.isNullOrEmpty(type.name()))
      {
        return klass.getSimpleName();
      }
      return type.name();
    }

    return null;

  }

  public static String getGQLInputTypeName(Class<?> klass)
  {

    GQLInputType type = klass.getAnnotation(GQLInputType.class);

    if (type != null)
    {
      if (Strings.isNullOrEmpty(type.name()))
      {
        return klass.getSimpleName();
      }
      return type.name();
    }

    return null;

  }

}
