package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.zrz.graphql.zulu.ZOutputField;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableElement;
import io.zrz.graphql.zulu.executable.ExecutableTypeUse;
import io.zrz.zulu.types.ZAnnotation;

/**
 * a selection in an operation from a prepared document that is bound to the executable model.
 *
 * @author theo
 *
 */

public interface ZuluSelection extends GQLPreparedSelection, ZOutputField {

  @Override
  ExecutableTypeUse fieldType();

  @Override
  default List<ZAnnotation> annotations() {
    return ZOutputField.super.annotations();
  }

  /**
   * returns true if this selection will result in a list of values, rather than a single one.
   */

  boolean isList();

  MethodHandle invoker();

  ExecutableElement element();

  /**
   * if this selection is bound to a java method, the element is it from.
   */

  Optional<? extends AnnotatedElement> origin();

  /**
   * calls the invoker directly.
   *
   * @param receiver
   * @param parentContext
   *
   * @return
   */

  default Object invoke(final ZuluRequestContext receiver, final Object parentContext) {
    try {
      return this.invoker().invoke(Objects.requireNonNull(receiver), Objects.requireNonNull(parentContext));
    }
    catch (final RuntimeException ex) {
      throw ex;
    }
    catch (final Throwable ex) {
      throw new RuntimeException(ex);
    }
  }

  void apply(ZuluSelectionVisitor.VoidVisitor visitor);

  <T> void apply(ZuluSelectionVisitor.ConsumerVisitor<T> visitor, T value);

  <R> R apply(ZuluSelectionVisitor.SupplierVisitor<R> visitor);

  <T, R> R apply(ZuluSelectionVisitor.FunctionVisitor<T, R> visitor, T value);

}
