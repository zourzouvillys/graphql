package io.zrz.zulu.schema.binding;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.zulu.schema.ResolvedSchema;

public class BoundDocument {

  private final ResolvedSchema schema;
  private final ImmutableList<BoundOperation> ops;
  private final ImmutableMap<String, BoundOperation> named;

  // the fragments in this doc.
  private final ImmutableMap<String, BoundFragment> fragments;
  private final BoundOperation defaultOperation;

  public BoundDocument(ResolvedSchema schema, GQLDocument doc) {

    this.schema = schema;

    BoundBuilder b = new BoundBuilder(schema, doc, this);

    this.fragments = b.fragments == null
        ? ImmutableMap.of()
        : ImmutableMap.copyOf(b.fragments);

    this.ops = doc.operations()
        .stream()
        .sequential()
        .map(op -> new BoundOperation(this, op, b))
        .collect(ImmutableList.toImmutableList());

    BoundOperation dop = null;

    for (BoundOperation op : this.ops) {

      if (StringUtils.isBlank(op.operationName())) {
        if (dop != null) {
          throw new IllegalArgumentException("more than one default query in document");
        }
        if (op.operationType() != GQLOpType.Query) {
          throw new IllegalArgumentException("the default (unnamed) query may only be a query");
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

  public BoundOperation operation(String operationName) {
    return this.named.get(operationName);
  }

  public ResolvedSchema schema() {
    return this.schema;
  }

}
