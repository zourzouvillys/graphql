package io.zrz.graphql.zulu.binding;

import java.lang.reflect.Method;

import io.zrz.graphql.zulu.executable.JavaExecutableUtils;

public interface OutputFieldFilter {

  default OutputFieldFilter forSupertype(final JavaBindingType t) {
    return this;
  }

  default boolean shouldInclude(final JavaBindingMethodAnalysis m) {

    if (m.origin().isPresent()) {

      final Method method = m.origin().get();

      final Method declaring = JavaExecutableUtils.getDeclaredMethod(method);

      if (declaring.getDeclaringClass().equals(Object.class)) {
        return false;
      }
      else if (declaring.getDeclaringClass().equals(Comparable.class)) {
        return false;
      }

      if (method.getName().equals("compareTo")) {
        System.err.println(declaring.getDeclaringClass());
        throw new IllegalArgumentException();
      }

    }

    return true;
  }

}
