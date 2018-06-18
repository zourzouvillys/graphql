package io.zrz.zulu.schema.model;

import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

public class ModelElementPrinter implements ModelElementVisitor.VoidVisitor {

  private static final String SEP = "  ";
  private PrintStream w;
  private int depth;

  public ModelElementPrinter(PrintStream w) {
    this.w = w;
    this.depth = 0;
  }

  @Override
  public void visitModelConnection(ModelConnection conn) {

    w.print("connection -> ");
    w.print(conn.connectionMeta().connectionType());
    w.print(", ");
    w.print(conn.connectionMeta().edgeType());
    w.print(", ");
    w.print(conn.connectionMeta().nodeType());
    w.println();

    depth++;
    conn.nodeSelections().forEach(sel -> {
      w.print(StringUtils.repeat(SEP, this.depth));
      w.print(sel);
      w.print(" : ");
      sel.accept(this);
    });
    depth--;

  }

  @Override
  public void visitModelObject(ModelObjectType obj) {
    w.print(obj.type());
    w.print(" ");
    w.print(obj.selection().fieldSelection().returnType().isList());
    w.println();
    depth++;
    obj.fields().forEach((fieldName, element) -> {
      w.print(StringUtils.repeat(SEP, this.depth));
      w.print(fieldName);
      w.print(" : ");
      element.accept(this);
    });
    depth--;
  }

  @Override
  public void visitModelScalar(ModelScalarField scalar) {
    w.println(scalar.scalarType());
  }

  @Override
  public void visitModelRoot(ModelRootType root) {
    w.print(root.type());
    w.print(" ");
    w.print(root.selection());
    w.println();
    depth++;
    root.selections().forEach((element) -> {
      w.print(StringUtils.repeat(SEP, this.depth));
      // w.print(fieldName);
      // w.print(" : ");
      element.accept(this);
    });
    depth--;
  }

}
