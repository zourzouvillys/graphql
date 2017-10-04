package io.zrz.graphql.core.decl;

import io.zrz.graphql.core.utils.DefaultTypeDeclarationVisitor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GQLDeclarationVisitors
{

  /**
   * visitor which returns true if the given decl is undoubtedly a relay node.
   * 
   * @return
   */

  public static GQLTypeDeclarationVisitor<Boolean> isRelayNode()
  {

    return new DefaultTypeDeclarationVisitor<Boolean>(false) {

      @Override
      public Boolean visitObject(GQLObjectTypeDeclaration type)
      {
        return type.ifaces().stream().filter(d -> d.name().equals("Node")).findAny().isPresent();
      }

      @Override
      public Boolean visitInterface(GQLInterfaceTypeDeclaration type)
      {
        if (type.name().equals("Node"))
          return true;
        return type.ifaces().stream().filter(d -> d.name().equals("Node")).findAny().isPresent();
      }

    };
  }

}
