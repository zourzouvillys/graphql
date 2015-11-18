package io.joss.graphql.core.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * 
 * @author theo
 */

public interface TypeMaterializer<I>
{

  <O> O convert(TypeConverter converter, I from, Type targetType, Annotation[] annotations);

}
