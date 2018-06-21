package io.zrz.zulu.schema.validation;

import java.io.PrintStream;

import io.zrz.zulu.schema.binding.BoundElement;

/**
 * prints received diagnostics to an output stream
 *
 * @author theo
 *
 */

public class StreamDiagnosticListener implements DiagnosticListener<BoundElement> {

  private final PrintStream out;

  public StreamDiagnosticListener(final PrintStream out) {
    this.out = out;
  }

  @Override
  public void report(final Diagnostic<BoundElement> diag) {
    this.out.println(diag.toString());
  }

}
