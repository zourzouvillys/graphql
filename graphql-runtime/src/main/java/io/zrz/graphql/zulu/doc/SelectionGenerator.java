package io.zrz.graphql.zulu.doc;

import java.util.stream.Stream;

import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLSelectionVisitor;

class SelectionGenerator implements GQLSelectionVisitor<Stream<DefaultGQLPreparedSelection>> {

  private DefaultGQLPreparedOperation req;
  private DefaultGQLPreparedSelection parent;
  private GQLSelectionTypeCriteria condition;

  public SelectionGenerator(DefaultGQLPreparedOperation req) {
    this(req, null, null);
  }

  public SelectionGenerator(DefaultGQLPreparedOperation req, DefaultGQLPreparedSelection parent) {
    this(req, parent, null);
  }

  public SelectionGenerator(DefaultGQLPreparedOperation req, DefaultGQLPreparedSelection parent, GQLSelectionTypeCriteria condition) {
    this.req = req;
    this.parent = parent;
    this.condition = condition;
  }

  @Override
  public Stream<DefaultGQLPreparedSelection> visitFieldSelection(GQLFieldSelection arg) {
    return Stream.of(new DefaultGQLPreparedSelection(req, parent, this.condition, arg));
  }

  @Override
  public Stream<DefaultGQLPreparedSelection> visitFragmentSelection(GQLFragmentSpreadSelection frag) {

    GQLFragmentDefinition def = req.fragment(frag.name());

    return def.selections()
        .stream()
        .flatMap(x -> x.apply(new SelectionGenerator(req, parent, new GQLSelectionTypeCriteria(def.namedType()))));

  }

  @Override
  public Stream<DefaultGQLPreparedSelection> visitInlineFragment(GQLInlineFragmentSelection frag) {
    return frag
        .selections()
        .stream()
        .flatMap(x -> x.apply(new SelectionGenerator(req, parent, new GQLSelectionTypeCriteria(frag.typeCondition()))));
  }

}
