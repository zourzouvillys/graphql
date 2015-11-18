package io.joss.graphql.generator.java;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.stream.Collectors;

import io.joss.graphql.generator.java.codedom.AssignmentExpression;
import io.joss.graphql.generator.java.codedom.BodyDeclarationVisitor;
import io.joss.graphql.generator.java.codedom.ExpressionStatement;
import io.joss.graphql.generator.java.codedom.ExpressionVisitor;
import io.joss.graphql.generator.java.codedom.FieldDeclaration;
import io.joss.graphql.generator.java.codedom.FieldExpression;
import io.joss.graphql.generator.java.codedom.MethodDeclaration;
import io.joss.graphql.generator.java.codedom.Modifier;
import io.joss.graphql.generator.java.codedom.ReturnStatement;
import io.joss.graphql.generator.java.codedom.SimpleNameExpression;
import io.joss.graphql.generator.java.codedom.SingleVariableDeclaration;
import io.joss.graphql.generator.java.codedom.StatementVisitor;
import io.joss.graphql.generator.java.codedom.ThisExpression;
import io.joss.graphql.generator.java.codedom.TypeDeclaration;

public class JavaWriter implements BodyDeclarationVisitor<Void>, StatementVisitor<Void>, ExpressionVisitor<Void>
{

  private PrintStream out;
  private int depth = 0;

  JavaWriter(OutputStream out)
  {
    this.out = new PrintStream(out);
  }

  private void indent()
  {
    indent(0);
  }

  private void indent(int extra)
  {
    for (int i = 0; i < depth + extra; ++i)
      out.print("  ");
  }

  public void write(TypeDeclaration decl)
  {

    javadoc(decl.getJavadoc());

    for (String annotation : decl.getAnnotations())
    {
      indent();
      out.println(annotation);
    }

    indent();
    if (write(decl.getModifiers()))
    {
      out.print(" ");
    }
    if (!decl.isInterface())
    {
      out.print("class");
    }
    else
    {
      out.print("interface");
    }
    out.print(" ");
    out.print(decl.getName());

    if (decl.getSuperClass() != null)
    {
      if (decl.isInterface())
      {
        throw new IllegalStateException();
      }
      out.print(" extends ");
      out.print(decl.getSuperClass());
    }

    if (!decl.getSuperInterfaces().isEmpty())
    {
      if (decl.isInterface())
        out.print(" extends ");
      else
        out.print(" implements ");
      out.print(decl.getSuperInterfaces().stream().collect(Collectors.joining(", ")));
    }

    out.println();
    indent();
    out.println("{");

    out.println();

    decl.getBodyDeclarations().forEach(bd -> {

      bd.apply(this);
      out.println();

    });

    indent();

    out.println("}");
  }

  private boolean write(Set<Modifier> modifiers)
  {
    out.print(modifiers.stream().map(m -> m.toString().toLowerCase()).collect(Collectors.joining(" ")));
    return !modifiers.isEmpty();
  }

  @Override
  public Void visitMethod(MethodDeclaration method)
  {

    javadoc(method.getJavadoc());

    for (String annotation : method.getAnnotations())
    {
      indent(1);
      out.println(annotation);
    }

    indent(1);

    if (write(method.getModifiers()))
    {
      out.print(" ");
    }

    if (!method.isConstructor())
    {
      out.print(method.getType());
      out.print(" ");
    }

    out.print(method.getName());
    out.print("(");
    
    int i = 0;    
    for (SingleVariableDeclaration param : method.getParameters())
    {
      if (i++ > 0)
      {
        out.print(", ");
      }
      out.print(param.getType());
      out.print(" ");
      out.print(param.getName());
    }
    out.print(")");

    if (method.getBody() != null)
    {

      out.println();
      this.depth++;
      indent();
      out.println("{");
      this.depth++;

      method.getBody().getStatements().forEach(stmt -> {
        stmt.apply(this);
      });

      this.depth--;
      indent();
      out.println("}");
      this.depth--;

    }
    else
    {
      // no body for now.
      out.println(";");
    }
    return null;
  }

  void javadoc(String javadoc)
  {

    if (javadoc != null && !javadoc.trim().isEmpty())
    {
      indent(1);
      out.print("/**");
      out.print(javadoc);
      out.println("*/");
    }
  }

  @Override
  public Void visitTypeDeclaration(TypeDeclaration decl)
  {
    depth++;
    this.write(decl);
    depth--;
    return null;
  }

  @Override
  public Void visitField(FieldDeclaration field)
  {
    indent(1);
    if (write(field.getModifiers()))
    {
      out.print(" ");
    }
    out.print(field.getType());
    out.print(" ");
    out.print(field.getName());
    out.println(";");
    return null;
  }


  @Override
  public Void visitExpressionStatement(ExpressionStatement stmt)
  {
    indent();
    stmt.getExpression().apply(this);
    out.println(";");
    return null;
  }
  
  @Override
  public Void visitReturnStatement(ReturnStatement returnStatement)
  {
    indent();
    out.print("return");
    if (returnStatement.getExpression() != null)
    {
      out.print(" ");
      returnStatement.getExpression().apply(this);
    }
    out.println(";");
    return null;
  }

  @Override
  public Void visitFieldExpression(FieldExpression fieldExpression)
  {
    fieldExpression.getExpression().apply(this);
    out.print(".");
    out.print(fieldExpression.getName());
    return null;
  }

  @Override
  public Void visitThisExpression(ThisExpression thisExpression)
  {
    out.print("this");
    return null;
  }

  @Override
  public Void visitAssignmentExpression(AssignmentExpression expr)
  {
    expr.getLeft().apply(this);
    out.print(" ");
    out.print(expr.getOperator());
    out.print(" ");
    expr.getRight().apply(this);
    return null;
  }

  @Override
  public Void visitSimpleNameExpression(SimpleNameExpression expr)
  {
    out.print(expr.getName());
    return null;
  }


}
