package io.zrz.zulu.schema.validation;

public interface DiagnosticListener<S> {

  void report(Diagnostic<S> diag);

}
