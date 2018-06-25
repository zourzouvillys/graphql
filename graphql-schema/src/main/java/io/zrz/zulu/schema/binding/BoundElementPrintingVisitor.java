package io.zrz.zulu.schema.binding;

import java.io.PrintStream;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.zrz.zulu.schema.TypeUse;

public class BoundElementPrintingVisitor implements BoundElementVisitor.SupplierVisitor<String> {

  private static final String SEP = "  ";
  private final PrintStream w;
  private int depth = 0;

  public BoundElementPrintingVisitor(final PrintStream w) {
    this.w = w;
  }

  @Override
  public String visitOperation(final BoundOperation op) {
    this.w.print("[");
    this.w.print(op.operationType());
    this.w.print("] -> ");
    this.w.print(op.operationName());

    if (!op.vars().isEmpty()) {

      this.w.print("(");

      op.vars()
          .stream()
          .map(x -> this.toString(x))
          .collect(Collectors.collectingAndThen(Collectors.joining(", "), x -> {
            this.w.print(x);
            return null;
          }));

      this.w.print(")");

    }

    this.w.print(" : ");
    this.w.print(op.selectionType());
    this.w.println();
    this.depth++;
    op.selections().forEach(s -> s.accept(this));
    this.depth--;
    return null;
  }

  private String toString(final BoundVariable x) {
    final StringBuilder sb = new StringBuilder();
    sb.append("$").append(x.name()).append(": ");
    sb.append(this.toString(x.type()));
    if (x.defaultValue() != null) {
      sb.append(" = ");
      sb.append(x.defaultValue());
    }
    return sb.toString();
  }

  private String toString(final TypeUse type) {
    final StringBuilder sb = new StringBuilder();

    if (type.isList())
      sb.append("[");

    sb.append(type.targetType());

    if (type.isList())
      sb.append("]");

    if (type.isNullable()) {
      sb.append("!");
    }
    return sb.toString();
  }

  @Override
  public String visitObject(final BoundObjectSelection obj) {
    this.w.print(StringUtils.repeat(SEP, this.depth));
    this.w.print(obj.fieldName());
    this.w.print(" : ");
    this.w.print(obj.selectionType());
    this.w.println();
    this.depth++;
    obj.selections().forEach(s -> s.accept(this));
    this.depth--;
    return null;
  }

  @Override
  public String visitLeaf(final BoundLeafSelection leaf) {
    this.w.print(StringUtils.repeat(SEP, this.depth));
    this.w.print(leaf.fieldName());
    this.w.print(" : ");
    this.w.print(leaf.fieldType());
    this.w.println();
    return null;
  }

  @Override
  public String visitFragment(final BoundFragment frag) {
    this.w.print(StringUtils.repeat(SEP, this.depth));
    this.w.println(frag);
    frag.selections().forEach(s -> s.accept(this));
    return null;
  }

}
