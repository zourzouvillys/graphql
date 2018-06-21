package io.zrz.zulu.schema.validation;

public interface Diagnostic<S> {

  enum Kind {

    ERROR,

    WARNING,

    NOTE

  }

  Kind kind();

  S source();

  String message();

  String code();

}
