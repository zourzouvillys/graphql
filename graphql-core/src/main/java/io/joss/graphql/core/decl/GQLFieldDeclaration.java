package io.joss.graphql.core.decl;

import java.util.List;

import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.types.GQLTypeReference;

public interface GQLFieldDeclaration {

  public String name();

  public String description();

  public GQLTypeReference type();

  public String deprecationReason();

  public List<GQLDirective> directives();

}
