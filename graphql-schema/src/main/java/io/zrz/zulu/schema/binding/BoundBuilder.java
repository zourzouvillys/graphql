package io.zrz.zulu.schema.binding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.zrz.graphql.core.doc.GQLDocument;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelection;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.lang.GQLTypeVisitors;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;
import io.zrz.graphql.core.types.GQLTypeReference;
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
import io.zrz.zulu.schema.TypeUse;
import io.zrz.zulu.schema.validation.Diagnostic;
import io.zrz.zulu.schema.validation.DiagnosticListener;

class BoundBuilder implements DiagnosticListener<BoundElement> {

  Map<String, BoundFragment> fragments;

  private final Selector selector = new Selector();
  private final BoundDocument doc;
  private final GQLDocument input;

  private final ResolvedSchema schema;

  /**
   * listens for messages.
   */
  private final DiagnosticListener<BoundElement> listener;

  BoundBuilder(final ResolvedSchema schema, final GQLDocument input, final BoundDocument doc, final DiagnosticListener<BoundElement> listener) {
    this.schema = schema;
    this.input = input;
    this.doc = doc;
    this.listener = listener;
  }

  BoundFragment fragment(final String name) {

    if (this.fragments != null) {

      final BoundFragment frag = this.fragments.get(name);

      if (frag != null)
        return frag;

    }

    final BoundNamedFragment frag = new BoundNamedFragment(this.input.fragment(name), this);

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
    public BoundSelection visitFieldSelection(final GQLFieldSelection field, final BoundSelectionContainer parent) {

      // note that the parent type can be a union or interface if we're in a fragment spread.
      final ResolvedType resultType = parent.selectionType();

      return resultType.apply(this, parent, field);

    }

    @Override
    public BoundSelection visitFragmentSelection(final GQLFragmentSpreadSelection frag, final BoundSelectionContainer parent) {
      return BoundBuilder.this.fragment(frag.name());
    }

    @Override
    public BoundSelection visitInlineFragment(final GQLInlineFragmentSelection inline, final BoundSelectionContainer parent) {
      return new BoundInlineFragment(inline, parent, BoundBuilder.this);
    }

    // ---

    // select a field value.

    @Override
    public BoundSelection visit(final ResolvedObjectType type, final BoundSelectionContainer parent, final GQLFieldSelection sel) {

      final ResolvedObjectField field = type.field(sel.name());

      if (field == null) {
        BoundBuilder.this.report(BoundDiagnosticCode.UNKNOWN_FIELD, sel, type);
        return null;
      }

      // if we're a type of interface, allow fields. union is also allowed but only for a spread to a potential type.

      if (sel.selections().isEmpty()) {
        return new BoundLeafSelection(field, sel, BoundBuilder.this);
      }

      return new BoundObjectSelection(parent, field, sel, BoundBuilder.this);

    }

    @Override
    public BoundSelection visit(final ResolvedInterfaceType type, final BoundSelectionContainer parent, final GQLFieldSelection sel) {

      final ResolvedObjectField field = type.field(sel.name());

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
    public BoundSelection visit(final ResolvedEnumType type, final BoundSelectionContainer arg1, final GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

    @Override
    public BoundSelection visit(final ResolvedInputType type, final BoundSelectionContainer arg1, final GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

    @Override
    public BoundSelection visit(final ResolvedScalarType type, final BoundSelectionContainer arg1, final GQLFieldSelection arg2) {
      BoundBuilder.this.report(BoundDiagnostic.selectionScope(BoundDiagnosticCode.SUBSELECTION_ON_SCALAR, arg2));
      return null;
    }

    @Override
    public BoundSelection visit(final ResolvedUnionType type, final BoundSelectionContainer arg1, final GQLFieldSelection arg2) {
      throw new IllegalArgumentException();
    }

  }

  void add(final BoundNamedFragment frag, final String name) {
    if (this.fragments == null) {
      this.fragments = new HashMap<>();
    }

    // validate this fragment to ensure there are no cycles.

    if (frag.selections().stream().anyMatch(sub -> sub.hasFragmentCycle(frag))) {
      throw new IllegalArgumentException("found fragment cycle on '" + name + "'");
    }

    this.fragments.put(frag.name(), frag);

  }

  ResolvedType resolve(final GQLDeclarationRef namedType) {
    final String typeName = namedType.apply(GQLTypeVisitors.rootType()).name();
    final ResolvedType type = this.schema.type(Objects.requireNonNull(typeName, namedType.toString()));
    if (type == null) {
      throw new IllegalArgumentException("invalid named type '" + typeName + "'");
    }
    return type;
  }

  ResolvedSchema schema() {
    return this.schema;
  }

  public TypeUse resolve(final GQLTypeReference type) {
    return type.apply(new Visitor());
  }

  private class Visitor implements GQLTypeVisitor<TypeUse> {

    @Override
    public TypeUse visitNonNull(final GQLNonNullType type) {
      return type.type().apply(this);
    }

    @Override
    public TypeUse visitList(final GQLListType type) {
      return type.type().apply(this);
    }

    @Override
    public TypeUse visitDeclarationRef(final GQLDeclarationRef type) {
      return new TypeUse(BoundBuilder.this.schema(), BoundBuilder.this.resolve(type), true, 0);
    }

  }

  @Override
  public void report(final Diagnostic<BoundElement> diag) {
    this.listener.report(diag);
  }

  public BoundOperation createOperation(final GQLOperationDefinition op) {
    final ResolvedObjectType rootType = (ResolvedObjectType) this.schema().operationType(op.type());
    if (rootType == null) {
      this.report(op, BoundDiagnosticCode.UNKNOWN_TYPE);
      return null;
    }
    return new BoundOperation(this.doc, op, rootType, this);
  }

  private void report(final GQLOperationDefinition op, final BoundDiagnosticCode code) {
    this.listener.report(BoundDiagnostic.operationScope(code, op));
  }

  public void report(final BoundDiagnosticCode code, final GQLFieldSelection selection, final ResolvedObjectType type) {
    this.listener.report(BoundDiagnostic.selectionScope(code, selection));
  }
}
