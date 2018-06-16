package io.zrz.zulu.schema.binding;

import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

public class BoundDocumentPrinter implements BoundSelection.VoidVisitor {

  private PrintStream w;
  private int depth = 0;

  public BoundDocumentPrinter(PrintStream w) {
    this.w = w;
  }

  public void print(BoundDocument doc) {

    doc.operations().forEach(op -> print(op));

  }

  private void print(BoundOperation op) {

    w.println();
    w.print(op.operationType());

    if (op.operationName() != null) {
      w.print(" ");
      w.print(op.operationName());
    }

    w.print(" : ");
    w.print(op.selectionType().targetType());

    w.println(" {");

    ++this.depth;
    op.selections().forEach(sel -> sel.apply(this));
    --this.depth;

    w.println("}");

  }

  @Override
  public void apply(BoundLeafSelection sel) {
    w.print(StringUtils.repeat("  ", this.depth));
    w.print(sel.fieldName());
    w.print(" : ");
    w.print(sel.fieldType());
    w.println();
  }

  @Override
  public void apply(BoundObjectSelection sel) {
    w.print(StringUtils.repeat("  ", this.depth));
    w.print(sel.fieldName());

    if (!sel.params().isEmpty()) {
      w.print("(");

      for (int i = 0; i < sel.params().size(); ++i) {

        if (i > 0)
          w.print(", ");

        BoundFieldArgument p = sel.params().get(i);

        w.print(p.name());
        w.print(": ");

        if (p.typeUse().dims() > 0) {
          w.print("[");
        }

        w.print(p.type().typeName());

        if (p.typeUse().dims() > 0) {
          w.print("]");
        }

        if (!p.typeUse().isNullable()) {
          w.print("!");
        }

      }

      w.print(")");
    }

    w.print(" : ");
    w.print(sel.selectionType());
    w.println(" {");
    ++this.depth;
    sel.selections().forEach(sub -> sub.apply(this));
    --this.depth;
    w.print(StringUtils.repeat("  ", this.depth));
    w.println("}");
  }

  @Override
  public void apply(BoundInlineFragment sel) {
    w.print(StringUtils.repeat("  ", this.depth));
    w.print("... on ");
    w.print(sel.spreadType().typeName());
    w.println(" {");
    ++depth;
    sel.selections().forEach(sub -> sub.apply(this));
    --depth;
    w.print(StringUtils.repeat("  ", this.depth));
    w.println("}");
  }

  @Override
  public void apply(BoundNamedFragment sel) {
    w.print(StringUtils.repeat("  ", this.depth));
    w.print("... on ");
    w.print(sel.spreadType().typeName());
    w.println(" {");
    ++depth;
    sel.selections().forEach(sub -> sub.apply(this));
    --depth;
    w.print(StringUtils.repeat("  ", this.depth));
    w.println("}");

  }

}
