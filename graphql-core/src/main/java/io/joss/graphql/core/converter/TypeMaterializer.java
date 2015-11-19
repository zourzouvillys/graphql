package io.joss.graphql.core.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A {@link TypeMaterializer} converts instances of a specified type into generic types. 
 */

public interface TypeMaterializer<I>
{

  <O> O convert(TypeConverter converter, I from, Type targetType, Annotation[] annotations);

}
