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
import io.zrz.graphql.zulu.annotations.GQLExtension;

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
  private final TypeToken<?> type;
  private final ImmutableList<JavaBindingMethodAnalysis> methods;
  private final Class<?> rawClass;
  private Optional<JavaBindingTypeUse> superclass;
  private final ImmutableList<JavaBindingTypeUse> ifaces;

  public JavaBindingClassAnalysis(final Type type) {

    this.type = TypeToken.of(type);
    this.rawClass = this.type.getRawType();

    if (this.rawClass.getAnnotatedSuperclass() != null) {
      this.superclass = Optional.of(new JavaBindingTypeUse(this.type, this.rawClass.getAnnotatedSuperclass()));
    }
    else {
      this.superclass = Optional.empty();
    }

    this.ifaces = Arrays.stream(this.rawClass.getAnnotatedInterfaces())
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
    return this.rawClass.getAnnotationsByType(GQLExtension.class).length > 0;
  }

  /**
   * true if this class should be included for auto-scanning of fields.
   *
   * this is true if we were manually registered, or have a GQLAutoScan method.
   *
   */

  public boolean isAutoInclude() {
    return Arrays
        .stream(this.rawClass.getAnnotationsByType(GQLAutoScan.class))
        .findAny()
        .map(x -> x.value())
        .orElse(false);
  }

  public <T extends Annotation> boolean isAnnotationPresent(final Class<T> annotationClass) {
    return this.rawClass.isAnnotationPresent(annotationClass);
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

  private static JavaBindingClassAnalysis analize(final Type type) {
    return new JavaBindingClassAnalysis(type);
  }

  public static JavaBindingClassAnalysis lookup(final Type type) {
    return cache.computeIfAbsent(type, JavaBindingClassAnalysis::analize);
  }

  public TypeToken<?> javaType() {
    return this.type;
  }

  @Override
  public String toString() {

    return "JavaClassAnalysis{" + this.javaType() + "}";

  }

  public <T extends Annotation> ImmutableList<T> annotations(final Class<T> annotationClass) {
    return ImmutableList.copyOf(this.type.getRawType().getAnnotationsByType(annotationClass));
  }

}
