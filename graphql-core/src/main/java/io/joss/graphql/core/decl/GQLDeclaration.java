package io.joss.graphql.core.decl;

public interface GQLDeclaration
{

  String name();
  
  String description();
  
  <R> R apply(GQLDeclarationVisitor<R> visitor);

}
