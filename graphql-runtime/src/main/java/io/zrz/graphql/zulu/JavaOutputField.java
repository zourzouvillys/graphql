package io.zrz.graphql.zulu;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.binding.JavaBindingInvoker;

/**
 * the specification for an input field on a zulu type.
 */

public interface JavaOutputField {

  /**
   * the field name. this is based on reflection/introspection, and may be renamed by the schema manager or other
   * plugins.
   */

  String fieldName();

  /**
   * the concrete java type that will be returned on execution of this field.
   */

  TypeToken<?> returnType();

  /**
   * the defined input parameters for this field.
   */

  default Stream<? extends JavaInputField> inputFields() {
    return Stream.empty();
  }

  /**
   * any documentation for this field, if available.
   */

  default String documentation() {
    return null;
  }

  /**
   * if this field is related to an underlying java element, return it.
   * 
   * if a handler replaces or generates dynamic code, this should point to the original element which triggered the
   * generation - if any.
   * 
   */

  default Optional<? extends AnnotatedElement> origin() {
    return Optional.empty();
  }

  /**
   * invokes
   * 
   * @param request
   * @param context
   * @param args
   * @return
   */

  <T extends Object, C, V> T invoke(V request, C context, Object... args);

  /**
   * provides an handle for building invocations of this field.
   */

  default JavaBindingInvoker invoker() {
    throw new RuntimeException("not implemented");
  }

}
