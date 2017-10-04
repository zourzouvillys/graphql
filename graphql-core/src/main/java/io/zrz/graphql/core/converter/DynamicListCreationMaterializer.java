package io.zrz.graphql.core.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLValue;

/**
 * Converts GQLListValue instances to List<>
 *
 * @author theo
 *
 */

public class DynamicListCreationMaterializer implements TypeMaterializer<GQLListValue> {

  @SuppressWarnings("unchecked")
  @Override
  public <O> O convert(TypeConverter converter, GQLListValue from, Type targetType, Annotation[] annotations) {

    if (!TypeUtils.isRawAssignableFrom(targetType, Collection.class)) {
      // only handle Collection types for now. arrays in the future.
      return null;
    }

    final ParameterizedType type = (ParameterizedType) targetType;

    // what we're expecting the inner type to be?
    final Type target = type.getActualTypeArguments()[0];

    final List<Object> child = new LinkedList<>();

    for (final GQLValue i : from.values()) {
      child.add(converter.convert(i, target));
    }

    return (O) child;

  }

}
