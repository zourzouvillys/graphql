package io.zrz.graphql.zulu.executable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.binding.JavaBindingInvoker;

public class ExecutableInvoker {

  private final JavaBindingInvoker invoker;
  private final ExecutableOutputField field;
  private final ReturnTypeUse returnTypeFilter;

  public ExecutableInvoker(final ExecutableOutputField field, final JavaBindingInvoker invoker, final ReturnTypeUse returnTypeFilter) {
    this.field = field;
    this.invoker = invoker;
    this.returnTypeFilter = returnTypeFilter;
  }

  /**
   * the java return type.
   */

  public TypeToken<?> returnType() {
    return this.invoker.returnType();
  }

  /**
   * the parameters required.
   */

  public Optional<ExecutableOutputFieldParameters> parameters() {
    return this.field.parameters();
  }

  /**
   * provides a method handle for binding this executable.
   *
   * @param returnType
   *                     the expected return type.
   * @param args
   *                     The parameters which will be passed in.
   */

  public <R> MethodHandle methodHandle() {
    return this.returnTypeFilter.filter(this.invoker.methodHandle());
  }

  /**
   * the return type dimensions
   *
   * @return
   */

  public int arity() {
    return this.returnTypeFilter.arity();
  }

  /**
   * the original origin of this method, if it's java based.
   */

  public Optional<? extends AnnotatedElement> origin() {
    return this.field.field().origin();
  }

}
