package io.joss.graphql.generator.java;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.stream.Collectors;

import io.joss.graphql.generator.java.codedom.BodyDeclarationVisitor;
import io.joss.graphql.generator.java.codedom.FieldDeclaration;
import io.joss.graphql.generator.java.codedom.MethodDeclaration;
import io.joss.graphql.generator.java.codedom.Modifier;
import io.joss.graphql.generator.java.codedom.SingleVariableDeclaration;
import io.joss.graphql.generator.java.codedom.TypeDeclaration;

public class JavaWriter implements BodyDeclarationVisitor<Void>
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

    indent(1);

    if (write(method.getModifiers()))
    {
      out.print(" ");
    }
    out.print(method.getType());
    out.print(" ");
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
    // no body for now.
    out.println(";");
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

}
