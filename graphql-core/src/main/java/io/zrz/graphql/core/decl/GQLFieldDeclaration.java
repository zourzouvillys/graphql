package io.zrz.graphql.core.decl;

import java.util.List;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;

public interface GQLFieldDeclaration {

  public String name();

  public String description();

  public GQLTypeReference type();

  public String deprecationReason();

  public List<GQLDirective> directives();

}
