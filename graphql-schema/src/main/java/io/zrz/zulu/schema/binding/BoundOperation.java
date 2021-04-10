package io.zrz.zulu.schema.binding;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.runtime.GQLOperationType;
import io.zrz.graphql.core.utils.NormalizedDefinitionPrinter;
import io.zrz.zulu.schema.ResolvedObjectType;
import io.zrz.zulu.schema.binding.BoundElementVisitor.SupplierVisitor;

public class BoundOperation implements BoundSelectionContainer, BoundElement {

  private final BoundDocument doc;
  private final @Nullable String name;
  private final ImmutableList<BoundVariable> vars;
  private final @Nullable GQLOpType type;
  private final ImmutableList<BoundSelection> selections;
  private final ResolvedObjectType rootType;
  private final String raw;
  private final String normalized;

  public BoundOperation(final BoundDocument doc, final GQLOperationDefinition op, final ResolvedObjectType rootType, final BoundBuilder b) {

    this.rootType = Objects.requireNonNull(rootType);

    this.doc = doc;
    this.name = op.name();
    this.type = op.type();

    this.raw = StringUtils.trimToNull(op.range().map(e -> e.content()).orElse(null));
    
    this.normalized = NormalizedDefinitionPrinter.normalize(op, true);

    this.vars = op.vars()
        .stream()
        .map(var -> new BoundVariable(this, var, b))
        .collect(ImmutableList.toImmutableList());

    this.selections = op.selections()
        .stream()
        .sequential()
        .map(sel -> BoundUtils.bind(this, sel, b))
        .filter(sel -> sel != null)
        .collect(ImmutableList.toImmutableList());

  }

  public Stream<BoundVariable> mandatoryParameters() {
    return this.vars()
        .stream()
        .filter(var -> !var.type().isNullable() && (var.defaultValue() == null));
  }

  public Stream<BoundVariable> optionalParameters() {
    return this.vars()
        .stream()
        .filter(var -> var.type().isNullable() || (var.defaultValue() != null));
  }

  public BoundDocument doc() {
    return this.doc;
  }

  public List<BoundVariable> vars() {
    return this.vars;
  }

  public String operationName() {
    return this.name;
  }

  public GQLOperationType operationType() {
    return this.type;
  }

  /**
   * the resolved type for this context.
   */

  @Override
  public ResolvedObjectType type() {
    return this.rootType;
  }

  @Override
  public List<BoundSelection> selections() {
    return this.selections;
  }

  @Override
  public String toString() {
    return this.type + " " + this.name;
  }

  @Override
  public ResolvedObjectType selectionType() {
    return this.rootType;
  }

  @Override
  public <R> R accept(final SupplierVisitor<R> visitor) {
    return visitor.visitOperation(this);
  }

  public String rawQuery() {
    return this.raw;
  }

  public String normalizedQuery() {
    return this.normalized;
  }

}
