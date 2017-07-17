package io.joss.graphql.core.converter;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.joss.graphql.core.schema.TypeVisitors;
import io.joss.graphql.core.schema.TypeVisitors.GenericReturnVisitor;
import io.joss.graphql.core.schema.model.EnumType;
import io.joss.graphql.core.schema.model.InputCompatibleType;
import io.joss.graphql.core.schema.model.InputType;
import io.joss.graphql.core.schema.model.InterfaceType;
import io.joss.graphql.core.schema.model.ObjectType;
import io.joss.graphql.core.schema.model.ScalarType;
import io.joss.graphql.core.schema.model.UnionType;

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

  public static TypeVisitors.GenericReturnVisitor<InputCompatibleType> inputCompatibleTypeExtractor() {
    return new GenericReturnVisitor<InputCompatibleType>() {

      @Override
      public InputCompatibleType visitUnionType(UnionType value) {
        return null;
      }

      @Override
      public InputCompatibleType visitScalarType(ScalarType value) {
        return value;
      }

      @Override
      public InputCompatibleType visitObjectType(ObjectType value) {
        return null;
      }

      @Override
      public InputCompatibleType visitInterfaceType(InterfaceType value) {
        return null;
      }

      @Override
      public InputCompatibleType visitInputType(InputType value) {
        return value;
      }

      @Override
      public InputCompatibleType visitEnumType(EnumType value) {
        return value;
      }

    };
  }

}
