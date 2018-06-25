package io.zrz.graphql.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.graphql.core.doc.GQLDefinitionVisitor;
import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.doc.GQLFieldSelection;
import io.zrz.graphql.core.doc.GQLFragmentDefinition;
import io.zrz.graphql.core.doc.GQLFragmentSpreadSelection;
import io.zrz.graphql.core.doc.GQLInlineFragmentSelection;
import io.zrz.graphql.core.doc.GQLOpType;
import io.zrz.graphql.core.doc.GQLOperationDefinition;
import io.zrz.graphql.core.doc.GQLSelectionVisitor;
import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.value.GQLBooleanValue;
import io.zrz.graphql.core.value.GQLEnumValueRef;
import io.zrz.graphql.core.value.GQLFloatValue;
import io.zrz.graphql.core.value.GQLIntValue;
import io.zrz.graphql.core.value.GQLListValue;
import io.zrz.graphql.core.value.GQLObjectValue;
import io.zrz.graphql.core.value.GQLStringValue;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.graphql.core.value.GQLValueVisitor;
import io.zrz.graphql.core.value.GQLVariableRef;

/**
 * generates a string that represents an operation, normalized so a hash can be generated over it for equivilence.
 *
 * @author theo
 *
 */
public class NormalizedDefinitionPrinter implements GQLDefinitionVisitor<Void>, GQLSelectionVisitor<Void>, GQLValueVisitor<Void> {

  private final PrintStream strm;
  private int nodeId;
  private final boolean includeName;

  public NormalizedDefinitionPrinter(final PrintStream strm, final boolean includeName) {
    this.strm = strm;
    this.includeName = includeName;
  }

  public static String normalize(final GQLOperationDefinition op) {
    return normalize(op, false);
  }

  public static String normalize(final GQLOperationDefinition op, final boolean includeName) {
    try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (final PrintStream ps = new PrintStream(baos)) {
        op.apply(new NormalizedDefinitionPrinter(ps, includeName));
      }
      return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
    catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Void visitOperation(final GQLOperationDefinition op) {

    final GQLOpType type = op.type();

    if (type == null) {
      this.strm.print("query");
    }
    else {
      this.strm.print(type.operationName());
    }

    if (op.name() != null && this.includeName) {
      this.strm.print(" ");
      this.strm.print(op.name());
    }

    if (!op.vars().isEmpty()) {
      this.strm.print('(');
      int count = 0;
      for (final GQLVariableDefinition arg : op.vars()) {
        if (count++ > 0) {
          this.strm.print(",");
        }
        this.strm.print('$');
        this.strm.print(arg.name());
        this.strm.print(':');
        this.strm.print(arg.type().apply(new TypeRefStringGenerator()));
        final GQLValue defaultValue = arg.defaultValue();
        if (defaultValue != null) {
          this.strm.print('=');
          defaultValue.apply(this);
        }
      }
      this.strm.print(')');
    }

    this.printDirectives(op.directives());

    this.strm.print("{");
    this.nodeId = 0;
    op.selections().forEach(sel -> sel.apply(this));
    this.strm.print("}");

    return null;
  }

  @Override
  public Void visitFragment(final GQLFragmentDefinition frag) {
    // TODO Auto-generated method stub
    return null;
  }

  //

  /**
   * Print the field selection out.
   */

  @Override
  public Void visitFieldSelection(final GQLFieldSelection selection) {

    if (this.nodeId++ > 0) {
      this.strm.print(",");
    }

    if (selection.alias() != null) {
      this.strm.print(selection.alias());
      this.strm.print(":");
    }

    this.strm.print(selection.name());

    if (!selection.args().isEmpty()) {
      this.strm.print('(');
      int count = 0;
      for (final GQLArgument arg : selection.args()) {
        if (count++ > 0) {
          this.strm.print(',');
        }
        this.strm.print(arg.name());
        this.strm.print(':');
        arg.value().apply(this);
      }
      this.strm.print(')');
    }

    if (!selection.directives().isEmpty()) {
      this.strm.print(" ");
      this.printDirectives(selection.directives());
    }

    if (!selection.selections().isEmpty()) {
      final int saved = this.nodeId;
      this.nodeId = 0;
      this.strm.print("{");
      selection.selections().forEach(sel -> sel.apply(this));
      this.strm.print("}");
      // no comma is needed after braces, but we add them anyway.
      this.nodeId = saved;
    }

    return null;

  }

  private String generateDirective(final GQLDirective directive) {

    final StringBuilder sb = new StringBuilder();

    sb.append("@").append(directive.name());

    if (!directive.args().isEmpty()) {
      final String x = directive.args().stream()
          .map(arg -> String.format("%s: %s", arg.name(), arg.value()))
          .collect(Collectors.joining(", "));
      sb.append('(').append(x).append(')');
    }

    return sb.toString();

  }

  private void printDirectives(final List<GQLDirective> directives) {
    this.strm.print(directives.stream().map(this::generateDirective).collect(Collectors.joining(" ")));
  }

  @Override
  public Void visitFragmentSelection(final GQLFragmentSpreadSelection selection) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitInlineFragment(final GQLInlineFragmentSelection selection) {
    // TODO Auto-generated method stub
    return null;
  }

  //

  @Override
  public Void visitVarValue(final GQLVariableRef value) {
    this.strm.print('$');
    this.strm.print(value.name());
    return null;
  }

  @Override
  public Void visitObjectValue(final GQLObjectValue value) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitListValue(final GQLListValue value) {
    final AtomicInteger val = new AtomicInteger(0);
    value.values().forEach(item -> {
      if (val.incrementAndGet() > 1) {
        this.strm.print(',');
      }
      item.apply(this);
    });
    return null;
  }

  @Override
  public Void visitBooleanValue(final GQLBooleanValue value) {
    this.strm.print(value == GQLBooleanValue.TRUE ? "true" : "false");
    return null;
  }

  @Override
  public Void visitIntValue(final GQLIntValue value) {
    this.strm.print(value.value());
    return null;
  }

  @Override
  public Void visitStringValue(final GQLStringValue value) {
    this.strm.print('"');
    this.strm.print(value.value().replace("\\", "\\\\").replace("\"", "\\\""));
    this.strm.print('"');
    return null;
  }

  @Override
  public Void visitFloatValue(final GQLFloatValue value) {
    this.strm.print(value.value());
    return null;
  }

  @Override
  public Void visitEnumValueRef(final GQLEnumValueRef value) {
    this.strm.print(value.value());
    return null;
  }

}
