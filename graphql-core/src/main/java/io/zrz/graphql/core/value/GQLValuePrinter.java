package io.zrz.graphql.core.value;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author theo
 *
 */

public class GQLValuePrinter {

  private static class Visitor implements GQLValueVisitor<Void> {

    StringBuilder sb = new StringBuilder();

    @Override
    public Void visitVarValue(GQLVariableRef value) {
      this.sb.append("$").append(value.name());
      return null;
    }

    @Override
    public Void visitObjectValue(GQLObjectValue value) {
      this.sb.append("{");
      final AtomicInteger pos = new AtomicInteger(0);
      value.entries().forEach((name, fval) -> {
        if (pos.getAndIncrement() > 0) {
          this.sb.append(",");
        }
        this.sb.append(" ").append(name).append(": ");
        fval.apply(this);
      });
      this.sb.append(" }");
      return null;
    }

    @Override
    public Void visitListValue(GQLListValue value) {
      this.sb.append("[");
      final AtomicInteger pos = new AtomicInteger(0);
      value.values().forEach((fval) -> {
        if (pos.getAndIncrement() > 0) {
          this.sb.append(",");
        }
        this.sb.append(" ");
        fval.apply(this);
      });
      this.sb.append(" ]");
      return null;
    }

    @Override
    public Void visitBooleanValue(GQLBooleanValue value) {
      this.sb.append(value.value() ? "true" : "false");
      return null;
    }

    @Override
    public Void visitIntValue(GQLIntValue value) {
      this.sb.append(value.value());
      return null;
    }

    @Override
    public Void visitStringValue(GQLStringValue value) {
      this.sb.append('"').append(value.value().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")).append('"');
      return null;
    }

    @Override
    public Void visitFloatValue(GQLFloatValue value) {
      this.sb.append(value.value());
      return null;
    }

    @Override
    public Void visitEnumValueRef(GQLEnumValueRef value) {
      this.sb.append(value.value());
      return null;
    }
  }

  public static String generate(GQLValue value) {
    final Visitor visitor = new Visitor();
    value.apply(visitor);
    return visitor.sb.toString();
  }

}
