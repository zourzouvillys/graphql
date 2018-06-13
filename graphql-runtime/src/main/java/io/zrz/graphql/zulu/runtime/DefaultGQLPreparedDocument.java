package io.zrz.graphql.zulu.runtime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLSelectedOperation;

class DefaultGQLPreparedDocument implements GQLPreparedDocument {

  private List<GQLPreparedOperation> operations;
  private GQLDocument document;
  private Map<String, GQLPreparedOperation> named;
  private Optional<GQLPreparedOperation> defaultOperation;

  public DefaultGQLPreparedDocument(GQLTypeResolver resolver, GQLDocument doc) {

    this.document = doc;

    this.operations = doc
        .operations()
        .stream()
        .map(op -> new DefaultGQLPreparedOperation(resolver, GQLSelectedOperation.query(doc, op)))
        .collect(Collectors.toList());

    this.named = this.operations.stream()
        .filter(op -> op.operationName().isPresent())
        .collect(Collectors.toMap(op -> op.operationName().get(), op -> op));

    this.defaultOperation = this.operations
        .stream()
        .filter(op -> !op.operationName().isPresent())
        .filter(op -> op.type() == GQLOpType.Query)
        .findFirst();

  }

  @Override
  public Optional<GQLPreparedOperation> defaultOperation() {
    return defaultOperation;
  }

  @Override
  public Optional<GQLPreparedOperation> operation(String name) {
    return Optional.ofNullable(this.named.get(name));
  }

  @Override
  public Stream<GQLPreparedOperation> operations() {
    return this.operations.stream();
  }

  @Override
  public GQLDocument document() {
    return this.document;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append("GQLPreparedDoc ops=").append(operations.size()).append("\n");

    this.operations.forEach(op -> {
      sb.append("\n");
      sb.append("op ");
      sb.append(op.operationName()).append(" ");
      sb.append(op.type()).append(" {\n");
      op.selection().forEach(sel -> dump(sel, sb));
      sb.append("\n}\n");
    });

    sb.append("\n");

    return sb.toString();

  }

  static void dump(GQLPreparedSelection sel, StringBuilder sb) {
    sb.append(sel);
    sel.subselections().forEach(sub -> dump(sub, sb));
  }

}
