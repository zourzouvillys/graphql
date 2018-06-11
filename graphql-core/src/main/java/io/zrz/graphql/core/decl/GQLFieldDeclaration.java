package io.zrz.graphql.core.decl;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.types.GQLTypeReference;

public interface GQLFieldDeclaration {

  String name();

  @Nullable
  String description();

  GQLTypeReference type();

  @Nullable
  String deprecationReason();

  List<GQLDirective> directives();

  GQLFieldDeclaration withName(String ref);

  GQLFieldDeclaration withDescription(String ref);

  GQLFieldDeclaration withType(GQLTypeReference ref);

  GQLFieldDeclaration withDeprecationReason(String ref);

  GQLFieldDeclaration withDirectives(GQLDirective... ref);

}
