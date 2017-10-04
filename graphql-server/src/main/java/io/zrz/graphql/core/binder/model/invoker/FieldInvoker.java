package io.zrz.graphql.core.binder.model.invoker;

import io.zrz.graphql.core.binder.runtime.InputObserver;

/**
 * Provides low level binding to a method.
 * 
 * There are two parts: (1) calculating the input parameters, and (2) calculating how we get parameters out.
 * 
 * Unlike normal method, we abstract the fact that multiple instance are passed at once. If the method is static we pass in the input as
 * either a list or an {@link InputObserver}, depending on which the method uses.
 * 
 * If the method is not static, we execute each one at a time.
 * 
 * @author theo
 *
 */

public interface FieldInvoker
{

  InputObserver<Object, Object> open();

}
