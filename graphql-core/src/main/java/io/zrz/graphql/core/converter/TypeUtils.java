package io.zrz.graphql.core.converter;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtils {

  /**
   * Given an input class, finds the usage of 'wanted', and returns the
   * annotated type number of param.
   */

  public static AnnotatedType getParamterOfInterface(Class<?> klass, Class<?> wanted, int param) {

    final AnnotatedType val = TypeUtils.getInterfaceType(klass, wanted);

    if (val == null) {
      // no base class implements the given interface
      return null;
    }

    //

    if (!(val instanceof AnnotatedParameterizedType)) {
      throw new IllegalArgumentException("is not a parameterized type");
    }

    final AnnotatedParameterizedType ptype = (AnnotatedParameterizedType) val;

    if (param >= ptype.getAnnotatedActualTypeArguments().length) {
      throw new IllegalArgumentException("too many");
    }

    return ptype.getAnnotatedActualTypeArguments()[param];

  }

  /**
   * Finds the {@link AnnotatedType} usage for a class which implements the
   * given interface.
   *
   * e.g, MyService<XXX> will return an annotatedType for MyService<XXX> if you
   * call getInterfaceType(myobj, MyService.class);
   *
   * @param klass
   * @param iface
   *
   * @return the value, or null if not found.
   */

  public static AnnotatedType getInterfaceType(Class<?> klass, Class<?> wanted) {

    for (final AnnotatedType iface : klass.getAnnotatedInterfaces()) {

      if (isRawEqual(iface.getType(), wanted)) {
        return iface;
      }

    }

    // TODO Auto-generated method stub
    return null;
  }

  /**
   * returns true if the given type's raw class is the provided "wanted class.
   *
   * @param type
   * @param wanted
   * @return
   */

  public static boolean isRawEqual(Type type, Class<?> wanted) {
    if (type.equals(wanted)) {
      return true;
    } else if (type instanceof ParameterizedType) {
      final ParameterizedType ptype = ((ParameterizedType) type);
      return ptype.getRawType().equals(wanted);
    }
    return false;
  }

  public static boolean isRawAssignableFrom(Type type, Class<?> wanted) {
    if (type instanceof Class<?> && wanted.isAssignableFrom((Class<?>) type)) {
      return true;
    } else if (type instanceof ParameterizedType) {
      final ParameterizedType ptype = ((ParameterizedType) type);
      return wanted.isAssignableFrom((Class<?>) ptype.getRawType());
    }
    return false;
  }

}
