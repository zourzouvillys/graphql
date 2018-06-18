package io.zrz.zulu.schema.binding;

import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

public class BoundElementPrintingVisitor implements BoundElementVisitor.SupplierVisitor<String> {

  private static final String SEP = "  ";
  private PrintStream w;
  private int depth = 0;

  public BoundElementPrintingVisitor(PrintStream w) {
    this.w = w;
  }

  @Override
  public String visitOperation(BoundOperation op) {
    w.print("[");
    w.print(op.operationType());
    w.print("] -> ");
    w.print(op.operationName());
    w.print(" : ");
    w.print(op.selectionType());
    w.println();
    this.depth++;
    op.selections().forEach(s -> s.accept(this));
    this.depth--;
    return null;
  }

  @Override
  public String visitObject(BoundObjectSelection obj) {
    w.print(StringUtils.repeat(SEP, this.depth));
    w.print(obj.fieldName());
    w.print(" : ");
    w.print(obj.selectionType());
    w.println();
    this.depth++;
    obj.selections().forEach(s -> s.accept(this));
    this.depth--;
    return null;
  }

  @Override
  public String visitLeaf(BoundLeafSelection leaf) {
    w.print(StringUtils.repeat(SEP, this.depth));
    w.print(leaf.fieldName());
    w.print(" : ");
    w.print(leaf.fieldType());
    w.println();
    return null;
  }

  @Override
  public String visitFragment(BoundFragment frag) {
    w.print(StringUtils.repeat(SEP, this.depth));
    w.println(frag);
    frag.selections().forEach(s -> s.accept(this));
    return null;
  }

}
