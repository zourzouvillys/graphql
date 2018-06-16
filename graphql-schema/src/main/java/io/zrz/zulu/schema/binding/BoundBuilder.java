package io.zrz.zulu.schema.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.lang.GQLTypeVisitors;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.zulu.schema.ResolvedEnumType;
import io.zrz.zulu.schema.ResolvedInputType;
import io.zrz.zulu.schema.ResolvedInterfaceType;
import io.zrz.zulu.schema.ResolvedObjectField;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.ResolvedScalarType;
import io.zrz.zulu.schema.ResolvedSchema;
import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.ResolvedUnionType;
import io.zrz.zulu.schema.SchemaType;

class BoundBuilder {

  Map<String, BoundFragment> fragments;

  private final Selector selector = new Selector();
  private final BoundDocument doc;
  private final GQLDocument input;

  private ResolvedSchema schema;

  BoundBuilder(ResolvedSchema schema, GQLDocument input, BoundDocument doc) {
    this.schema = schema;
    this.input = input;
    this.doc = doc;
  }

  BoundFragment fragment(String name) {

    if (this.fragments != null) {

      BoundFragment frag = fragments.get(name);

      if (frag != null)
        return frag;

    }

    BoundNamedFragment frag = new BoundNamedFragment(input.fragment(name), this);

    // ensure there are no cycles in this fragment.
    if (frag.selections().stream().anyMatch(sel -> sel.hasFragmentCycle(frag))) {
      throw new IllegalArgumentException("fragment cycle detected in '" + frag.name() + "'");
    }

    return frag;

  }

  GQLSelection.FunctionVisitor<BoundSelectionContainer, BoundSelection> selector() {
    return this.selector;
  }

  private class Selector implements
      GQLSelection.FunctionVisitor<BoundSelectionContainer, BoundSelection>,
      SchemaType.BiFunctionVisitor<BoundSelectionContainer, GQLFieldSelection, BoundSelection> {

    @Override
    public BoundSelection visitFieldSelection(GQLFieldSelection field, BoundSelectionContainer parent) {

      // note that the parent type can be a union or interface if we're in a fragment spread.
      ResolvedType resultType = parent.selectionType();

      return resultType.apply(this, parent, field);

    }

    @Override
    public BoundSelection visitFragmentSelection(GQLFragmentSpreadSelection frag, BoundSelectionContainer parent) {
      return fragment(frag.name());
    }

    @Override
    public BoundSelection visitInlineFragment(GQLInlineFragmentSelection inline, BoundSelectionContainer parent) {
      return new BoundInlineFragment(inline, parent, BoundBuilder.this);
    }

    // ---

    // select a field value.

    @Override
    public BoundSelection visit(ResolvedObjectType type, BoundSelectionContainer parent, GQLFieldSelection sel) {

      ResolvedObjectField field = type.field(sel.name());

      if (field == null) {
        throw new IllegalArgumentException("field '" + sel.name() + "' doesn't exist on '" + type.typeName() + "'");
      }

      // if we're a type of interface, allow fields. union is also allowed but only for a spread to a potential type.

      if (sel.selections().isEmpty()) {
        return new BoundLeafSelection(field, sel, BoundBuilder.this);
      }

      return new BoundObjectSelection(parent, field, sel, BoundBuilder.this);

    }

    @Override
    public BoundSelection visit(ResolvedInterfaceType type, BoundSelectionContainer parent, GQLFieldSelection sel) {

      ResolvedObjectField field = type.field(sel.name());

      if (field == null) {
        throw new IllegalArgumentException("field '" + sel.name() + "' doesn't exist on '" + type.typeName() + "'");
      }

      // if we're a type of interface, allow fields. union is also allowed but only for a spread to a potential type.

      if (sel.selections().isEmpty()) {
        return new BoundLeafSelection(field, sel, BoundBuilder.this);
      }

      return new BoundObjectSelection(parent, field, sel, BoundBuilder.this);

    }

    @Override
    public BoundSelection visit(ResolvedEnumType type, BoundSelectionContainer arg1, GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

    @Override
    public BoundSelection visit(ResolvedInputType type, BoundSelectionContainer arg1, GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

    @Override
    public BoundSelection visit(ResolvedScalarType type, BoundSelectionContainer arg1, GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

    @Override
    public BoundSelection visit(ResolvedUnionType type, BoundSelectionContainer arg1, GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

  }

  void add(BoundNamedFragment frag, String name) {
    if (fragments == null) {
      this.fragments = new HashMap<>();
    }

    // validate this fragment to ensure there are no cycles.

    if (frag.selections().stream().anyMatch(sub -> sub.hasFragmentCycle(frag))) {
      throw new IllegalArgumentException("found fragment cycle on '" + name + "'");
    }

    this.fragments.put(frag.name(), frag);

  }

  ResolvedType resolve(GQLDeclarationRef namedType) {
    String typeName = namedType.apply(GQLTypeVisitors.rootType()).name();
    ResolvedType type = this.schema.type(Objects.requireNonNull(typeName, namedType.toString()));
    if (type == null) {
      throw new IllegalArgumentException("invalid named type '" + typeName + "'");
    }
    return type;
  }

  ResolvedSchema schema() {
    return this.schema;
  }

}
