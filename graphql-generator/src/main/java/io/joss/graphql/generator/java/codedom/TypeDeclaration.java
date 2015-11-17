package io.joss.graphql.generator.java.codedom;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * <pre>
 * TypeDeclaration:
 *   ClassDeclaration
 *   InterfaceDeclaration
 * ClassDeclaration:
 * [ Javadoc ] { ExtendedModifier } class Identifier
 *           [ < TypeParameter { , TypeParameter } > ]
 *           [ extends Type ]
 *           [ implements Type { , Type } ]
 *           { { ClassBodyDeclaration | ; } }
 * InterfaceDeclaration:
 * [ Javadoc ] { ExtendedModifier } interface Identifier
 *           [ < TypeParameter { , TypeParameter } > ]
 *           [ extends Type { , Type } ]
 *           { { InterfaceBodyDeclaration | ; } }
 * </pre>
 */

@Builder(builderClassName = "Builder")
@Getter
public class TypeDeclaration implements BodyDeclaration
{
  
  private final String javadoc;

  @Singular
  private final List<String> annotations;

  private final String superClass;

  @Singular
  private final List<String> superInterfaces;

  @Singular
  private final Set<Modifier> modifiers;
  

  /**
   * The name of this type.
   */

  private final String name;

  /**
   * If this is an interface or a class.
   */

  private final boolean isInterface;

  /**
   * represents something in the body of a class or interface (method, field, etc).
   */

  @Singular
  private List<BodyDeclaration> bodyDeclarations;

  @Override
  public <R> R apply(BodyDeclarationVisitor<R> visitor)
  {
    return visitor.visitTypeDeclaration(this);
  }

}
