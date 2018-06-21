package io.zrz.zulu.schema.validation;

import java.util.LinkedList;
import java.util.List;

public class ValidationCollector<S> implements DiagnosticListener<S> {

  private final List<Diagnostic<S>> diagnostics = new LinkedList<>();

  @Override
  public void report(final Diagnostic<S> diag) {
    this.diagnostics.add(diag);
  }

  public List<Diagnostic<S>> diagnostics() {
    return this.diagnostics;
  }

}
