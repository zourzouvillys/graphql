package io.zrz.graphql.core.doc;

public interface GQLDefinition
{

  <R> R apply(GQLDefinitionVisitor<R> visitor);

}
