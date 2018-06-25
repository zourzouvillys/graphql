package io.zrz.graphql.zulu.binding;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaOutputField;

/**
 * a logical representation of a java type, dealing only with java layer (doesn't know anything about GraphQL).
 */

public class JavaBindingType {

  private final TypeToken<?> type;
  private final JavaBindingClassAnalysis analysis;
  private final Set<JavaBindingType> supertypes = new HashSet<>();
  private final JavaBindingProvider generator;
  private boolean exported;

  public JavaBindingType(final JavaBindingProvider generator, final TypeToken<?> type) {
    this.generator = generator;
    this.type = type;
    this.analysis = JavaBindingClassAnalysis.lookup(type.getRawType());
  }

  /**
   * the resulting analysis of this type.
   */

  public JavaBindingClassAnalysis analysis() {
    return this.analysis;
  }

  /**
   * adds an interface.
   */

  public void processInterface(final JavaBindingType type) {

    if (type.type.isSubtypeOf(type.type)) {
      this.supertypes.add(type);
    }

  }

  /**
   * all of the supertypes of this java type.
   */

  public Stream<JavaBindingTypeUse> supertypes() {
    return this.analysis.superTypes();
  }

  /**
   * returns all of the fields in this logical type, which includes methods from all mixin supertypes which are not
   * hidden by methods declared in this class as well as any generated extensions for this type.
   */

  public Stream<? extends JavaOutputField> outputFields(final OutputFieldFilter filter) {
    return Stream.concat(
        this.analysis.methods()
            .filter(m -> !Modifier.isStatic(m.method.getModifiers()))
            .filter(m -> filter.shouldInclude(m)),
        this.analysis
            .superTypes()
            .filter(a -> a.isMixin())
            .map(a -> this.generator.include(a.typeToken()))
            .flatMap(t -> t.outputFields(filter.forSupertype(t))));
  }

  /**
   *
   */

  @Override
  public String toString() {
    return "JavaType{token=" + this.type + ", exported=" + this.exported + "}";
  }

}
