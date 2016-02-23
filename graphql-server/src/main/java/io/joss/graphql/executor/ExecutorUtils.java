package io.joss.graphql.executor;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import io.joss.graphql.core.binder.annotatons.GQLInputType;
import io.joss.graphql.core.binder.annotatons.GQLType;

public final class ExecutorUtils
{

  private static final CacheLoader<Class<?>, String> loader = new CacheLoader<Class<?>, String>() {
    public String load(Class<?> klass)
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
      return "";
    }
  };

  LoadingCache<Class<?>, String> cache = CacheBuilder.newBuilder().build(loader);

  public static String getGQLTypeName(Class<?> klass)
  {
    try
    {
      return Strings.emptyToNull(loader.load(klass));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
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