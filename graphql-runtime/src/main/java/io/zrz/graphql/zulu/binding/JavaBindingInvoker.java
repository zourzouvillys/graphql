package io.zrz.graphql.zulu.binding;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import io.zrz.graphql.zulu.ZuluUtils;

/**
 * a java bound method exposes the context and a set of parameters which can be used to create a method handle.
 * 
 * @author theo
 *
 */

public class JavaBindingInvoker {

  private JavaBindingMethodAnalysis method;
  private MethodHandle handle;
  private ImmutableList<JavaBindingParameter> parameters;

  /**
   * 
   */

  JavaBindingInvoker(JavaBindingMethodAnalysis method) {
    this.method = method;
    this.handle = ZuluUtils.unreflect(MethodHandles.lookup(), method.method);
    this.parameters = ImmutableList.copyOf(method.parameters());
  }

  /**
   * annotations on the executable itself.
   */

  public Annotation[] executableAnnotations() {
    return this.method.annotations();
  }

  /**
   * annotations on the return type.
   */

  public Annotation[] returnTypeAnnotations() {
    return this.method.returnTypeUse();
  }

  /**
   * the java return type.
   */

  public TypeToken<?> returnType() {
    return this.method.returnType();
  }

  /**
   * the parameters required.
   */

  public List<JavaBindingParameter> parameters() {
    return this.parameters;
  }

  /**
   * the handle to invoke.
   */

  public MethodHandle methodHandle() {
    return this.handle;
  }

  /**
   * the method type.
   */

  public MethodType methodType() {
    return this.handle.type();
  }

}
