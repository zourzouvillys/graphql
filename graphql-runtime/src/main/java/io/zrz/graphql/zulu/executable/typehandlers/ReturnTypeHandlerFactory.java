package io.zrz.graphql.zulu.executable.typehandlers;

import java.lang.invoke.MethodHandle;

import com.google.common.reflect.TypeToken;

public interface ReturnTypeHandlerFactory<T, R> {

  ReturnTypeHandler<R> createHandler(TypeToken<?> type);

  interface ReturnTypeHandler<T> {

    TypeToken<?> unwrap();

    MethodHandle adapt();

    int arity();

  }

}
