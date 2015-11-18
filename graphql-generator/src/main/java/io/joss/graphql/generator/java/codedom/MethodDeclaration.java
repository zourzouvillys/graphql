package io.joss.graphql.generator.java.codedom;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class MethodDeclaration implements BodyDeclaration
{
  
  private final boolean constructor;

  private final String javadoc;
  @Singular
  private final Set<String> annotations;
  @Singular
  private final Set<Modifier> modifiers;
  private String type;
  private String name;

  @Singular
  private List<SingleVariableDeclaration> parameters;

  private Block body;

  @Override
  public <R> R apply(BodyDeclarationVisitor<R> visitor)
  {
    return visitor.visitMethod(this);
  }

}
