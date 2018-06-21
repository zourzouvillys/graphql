package io.zrz.zulu.schema.binding;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.zulu.schema.validation.Diagnostic;

public class BoundDiagnostic implements Diagnostic<BoundElement> {

  private final BoundDiagnosticCode code;
  private final Kind kind;

  public BoundDiagnostic(final Kind kind, final BoundDiagnosticCode code) {
    this.kind = kind;
    this.code = code;
  }

  static BoundDiagnostic documentScope(final BoundDiagnosticCode code) {
    return new BoundDiagnostic(Kind.ERROR, code);
  }

  static BoundDiagnostic operationScope(final BoundDiagnosticCode code, final GQLOperationDefinition op) {
    return new BoundDiagnostic(Kind.ERROR, code);
  }

  static BoundDiagnostic selectionScope(final BoundDiagnosticCode code, final GQLFieldSelection selection) {
    return new BoundDiagnostic(Kind.ERROR, code);
  }

  @Override
  public Kind kind() {
    return this.kind;
  }

  @Override
  public BoundElement source() {
    return null;
  }

  @Override
  public String message() {
    return this.code().toString();
  }

  @Override
  public String code() {
    return this.code.name();
  }

  @Override
  public String toString() {
    return this.kind() + " " + this.code() + " " + this.message();
  }

}
