package io.zrz.graphql.zulu.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.annotations.GQLAutoScan;
import io.zrz.graphql.zulu.annotations.GQLOutputExtension;

/**
 * given a class, scans to generate an analysis of it for later processing.
 * 
 * results from this class are always a direct reflection of the code, so caching is enabled by default.
 * 
 * @author theo
 *
 */
public class JavaBindingClassAnalysis {

  private static final Map<Type, JavaBindingClassAnalysis> cache = new HashMap<>();
  private TypeToken<?> type;
  private ImmutableList<JavaBindingMethodAnalysis> methods;
  private Class<?> rawClass;
  private Optional<JavaBindingTypeUse> superclass;
  private ImmutableList<JavaBindingTypeUse> ifaces;

  public JavaBindingClassAnalysis(Type type) {

    this.type = TypeToken.of(type);
    this.rawClass = this.type.getRawType();

    if (rawClass.getAnnotatedSuperclass() != null) {
      this.superclass = Optional.of(new JavaBindingTypeUse(this.type, rawClass.getAnnotatedSuperclass()));
    }
    else {
      this.superclass = Optional.empty();
    }

    this.ifaces = Arrays.stream(rawClass.getAnnotatedInterfaces())
        .map(iface -> new JavaBindingTypeUse(this.type, iface))
        .collect(ImmutableList.toImmutableList());

    this.methods = JavaBindingUtils
        .listMethods(this.type)
        .map(m -> new JavaBindingMethodAnalysis(this, m))
        .collect(ImmutableList.toImmutableList());

    // don't add anything the method analysis will depend on here, make sure it's above the method asignment.

  }

  /**
   * all of the interfaces and subtypes that this class implements.
   */

  public Stream<JavaBindingTypeUse> superTypes() {
    if (this.superclass.isPresent())
      return Stream.concat(Stream.of(this.superclass.get()), this.ifaces.stream());
    return this.ifaces.stream();
  }

  /**
   * true if all methods in this class are extensions, otherwise false.
   */

  public boolean isExtensionClass() {
    return (rawClass.getAnnotationsByType(GQLOutputExtension.class).length > 0);
  }

  /**
   * true if this class should be included for auto-scanning of fields.
   * 
   * this is true if we were manually registered, or have a GQLAutoScan method.
   * 
   */

  public boolean isAutoInclude() {
    return Arrays
        .stream(rawClass.getAnnotationsByType(GQLAutoScan.class))
        .findAny()
        .map(x -> x.value())
        .orElse(false);
  }

  public <T extends Annotation> boolean isAnnotationPresent(Class<T> annotationClass) {
    return rawClass.isAnnotationPresent(annotationClass);
  }

  /**
   * all of the methods directly declared in this class.
   */

  public Stream<JavaBindingMethodAnalysis> methods() {
    return this.methods.stream();
  }

  /**
   * the methods which look like extensions
   */

  public Stream<JavaBindingMethodAnalysis> extensionFields() {
    if (this.isExtensionClass()) {
      return this.methods.stream();
    }
    return this.methods.stream().filter(m -> m.isExtensionMethod());
  }

  //// ----

  private static JavaBindingClassAnalysis analize(Type type) {
    return new JavaBindingClassAnalysis(type);
  }

  public static JavaBindingClassAnalysis lookup(Type type) {
    return cache.computeIfAbsent(type, JavaBindingClassAnalysis::analize);
  }

  public TypeToken<?> javaType() {
    return this.type;
  }

  @Override
  public String toString() {

    return "JavaClassAnalysis{" + javaType() + "}";

  }

  public <T extends Annotation> ImmutableList<T> annotations(Class<T> annotationClass) {
    return ImmutableList.copyOf(this.type.getRawType().getAnnotationsByType(annotationClass));
  }

}
