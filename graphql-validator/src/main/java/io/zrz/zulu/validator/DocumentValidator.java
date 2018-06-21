package io.zrz.zulu.validator;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.parser.GQLParser;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.binding.BoundDocument;
import io.zrz.zulu.schema.validation.DiagnosticListener;

/**
 * validates queries against a schema.
 *
 * @author theo
 *
 */

public class DocumentValidator {

  private final ResolvedSchema schema;

  public DocumentValidator(final ResolvedSchema schema) {
    this.schema = schema;
  }

  public BoundDocument validate(final GQLDocument doc, final DiagnosticListener<String> collector) {
    return new BoundDocument(this.schema, doc);
  }

  public BoundDocument validate(final String doc, final DiagnosticListener<String> collector) {
    return this.validate(GQLParser.defaultParser().parse(doc), collector);
  }

}
