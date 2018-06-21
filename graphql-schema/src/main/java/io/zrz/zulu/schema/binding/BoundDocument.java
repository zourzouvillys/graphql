package io.zrz.zulu.schema.binding;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.validation.DiagnosticListener;
import io.zrz.zulu.schema.validation.StreamDiagnosticListener;

public class BoundDocument {

  private final ResolvedSchema schema;
  private final ImmutableList<BoundOperation> ops;
  private final ImmutableMap<String, BoundOperation> named;

  // the fragments in this doc.
  private final ImmutableMap<String, BoundFragment> fragments;
  private final BoundOperation defaultOperation;

  public BoundDocument(final ResolvedSchema schema, final GQLDocument doc) {
    this(schema, doc, new StreamDiagnosticListener(System.err));
  }

  public BoundDocument(final ResolvedSchema schema, final GQLDocument doc, final DiagnosticListener<BoundElement> listener) {

    this.schema = schema;

    final BoundBuilder b = new BoundBuilder(schema, doc, this, listener);

    this.fragments = b.fragments == null
        ? ImmutableMap.of()
        : ImmutableMap.copyOf(b.fragments);

    this.ops = doc.operations()
        .stream()
        .sequential()
        .map(op -> b.createOperation(op))
        .filter(op -> op != null)
        .collect(ImmutableList.toImmutableList());

    BoundOperation dop = null;

    for (final BoundOperation op : this.ops) {

      if (StringUtils.isBlank(op.operationName())) {
        if (dop != null) {
          b.report(BoundDiagnostic.documentScope(BoundDiagnosticCode.MULTIPLY_DEFAULT_QUERIES));
        }
        if (op.operationType() != GQLOpType.Query) {
          b.report(BoundDiagnostic.documentScope(BoundDiagnosticCode.DEFAULT_OP_NOT_QUERY));
        }
        dop = op;
      }

    }

    this.defaultOperation = dop;

    this.named = this.ops
        .stream()
        .sequential()
        .filter(e -> !StringUtils.isBlank(e.operationName()))
        .collect(ImmutableMap.toImmutableMap(e -> e.operationName(), e -> e));

  }

  public List<BoundOperation> operations() {
    return this.ops;
  }

  public BoundOperation operation(final String operationName) {
    return this.named.get(operationName);
  }

  public ResolvedSchema schema() {
    return this.schema;
  }

}
