package io.zrz.graphql.zulu.binding;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.JavaInputField;
import io.zrz.graphql.zulu.JavaOutputField;
import io.zrz.graphql.zulu.annotations.GQLDocumentation;
import io.zrz.graphql.zulu.annotations.GQLExtension;
import io.zrz.graphql.zulu.annotations.GQLField;
import io.zrz.graphql.zulu.annotations.GQLTypeUse;

/**
 * a java based contribution.
 *
 * @author theo
 *
 */
public class JavaBindingMethodAnalysis implements JavaOutputField {

  final Method method;
  private final JavaBindingClassAnalysis owner;
  private JavaBindingInvoker invoker;
  private final TypeToken<?> context;

  /**
   *
   */

  public JavaBindingMethodAnalysis(final JavaBindingClassAnalysis owner, final Method method) {
    this.owner = owner;
    this.method = method;
    this.context = owner.javaType();
  }

  public JavaBindingMethodAnalysis(final JavaBindingClassAnalysis owner, final TypeToken<?> context, final Method method) {
    this.owner = owner;
    this.method = method;
    this.context = context;
  }

  /**
   * the context that this method needs.
   *
   * if it is an extension, this is the type that is needed for the method. otherwise, it's the owning class.
   *
   */

  public TypeToken<?> receiverType() {
    if (this.isExtensionMethod()) {
      return this.extensionReceiver();
    }
    return this.owner.javaType();
  }

  private TypeToken<?> extensionReceiver() {
    Preconditions.checkState(this.isExtensionMethod());
    Preconditions.checkState(this.method.getParameterCount() > 0, "invalid number of parameters for extension type (requires at least 1 for the receiver)");
    return TypeToken.of(this.method.getGenericParameterTypes()[0]);
  }

  @Override
  public String documentation() {
    return StringUtils.trimToNull(Arrays
        .stream(this.method.getAnnotationsByType(GQLDocumentation.class))
        .map(a -> a.value())
        .collect(Collectors.joining("\n\n")));
  }

  /**
   * if this field is an extension on another class.
   */

  public boolean isExtensionMethod() {
    return this.owner.isExtensionClass() || this.method.getAnnotationsByType(GQLExtension.class).length > 0;
  }

  /**
   * the raw parameters, which will exclude any java binding specific ones (e.g, receiver for extension method) but
   * include viewer, context, etc.
   */

  public List<JavaBindingParameter> parameters() {

    final Parameter[] params = this.method.getParameters();

    final ImmutableList<com.google.common.reflect.Parameter> tp = this.owner.javaType().method(this.method).getParameters();

    return IntStream.range(this.isExtensionMethod() ? 1 : 0, params.length)
        .mapToObj(idx -> new JavaBindingParameter(this, idx, params[idx], tp.get(idx).getType()))
        .collect(ImmutableList.toImmutableList());

  }

  /**
   * the expected input fields for this method.
   */

  @Override
  public Stream<? extends JavaInputField> inputFields() {
    return this.parameters()
        .stream();
  }

  public Annotation[] returnTypeUse() {
    return this.method.getAnnotatedReturnType().getAnnotations();
  }

  @Override
  public TypeToken<?> returnType() {
    return this.context.method(this.method).getReturnType();
    // return TypeToken.of(this.method.getGenericReturnType());
  }

  public String returnTypeAnnotations() {
    return Arrays.toString(this.returnTypeUse());
  }

  public String typeParameters() {

    return Arrays.stream(this.method.getTypeParameters())
        .map(x -> Arrays.toString(x.getBounds()))
        .collect(Collectors.joining(", "));

  }

  /**
   * the zulu name of this field.
   */

  @Override
  public String fieldName() {
    if (this.method.isAnnotationPresent(GQLField.class)) {
      for (final GQLField field : this.method.getAnnotationsByType(GQLField.class)) {
        if (field.value().length() > 0) {
          return field.value();
        }
      }
    }
    return this.method.getName();
  }

  public String returnTypeName() {
    if (this.method.getAnnotatedReturnType().isAnnotationPresent(GQLTypeUse.class)) {
      for (final GQLTypeUse u : this.method.getAnnotatedReturnType().getAnnotationsByType(GQLTypeUse.class)) {
        if (!u.name().isEmpty()) {
          return u.name();
        }
      }
    }
    return null;
  }

  /**
   * the handle for invoking this field.
   */

  @Override
  public JavaBindingInvoker invoker() {
    if (this.invoker == null) {
      this.invoker = new JavaBindingInvoker(this, this.context);
    }
    return this.invoker;
  }

  /**
   *
   */

  @Override
  public String toString() {
    if (this.isExtensionMethod()) {
      return this.getClass().getSimpleName() + "{" + this.receiverType() + "." + this.fieldName() + ", " + JavaBindingUtils.toString(this.method) + "}";
    }
    return this.getClass().getSimpleName() + "{" + this.owner + "." + this.fieldName() + ", " + JavaBindingUtils.toString(this.method) + "}";
  }

  public Annotation[] annotations() {
    return this.method.getAnnotations();
  }

  public boolean isAutoInclude() {
    return this.owner.isAutoInclude() || this.method.isAnnotationPresent(GQLField.class);
  }

  /**
   *
   */

  @Override
  public Optional<Method> origin() {
    return Optional.of(this.method);
  }

  @Override
  public <T, C, V> T invoke(final V request, final C context, final Object... args) {
    Preconditions.checkState(this.method != null);
    try {
      return (T) this.method.invoke(context, args);
    }
    catch (final InvocationTargetException ex) {
      try {
        throw ex.getCause();
      }
      catch (final RuntimeException inner) {
        throw inner;
      }
      catch (final Throwable inner) {
        throw new RuntimeException(inner);
      }
    }
    catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }

  public JavaBindingMethodAnalysis withContext(final TypeToken<?> context) {
    return new JavaBindingMethodAnalysis(this.owner, context, this.method);
  }

}
