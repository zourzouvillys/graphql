package io.joss.graphql.core.decl;

import java.util.List;

import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.parser.GQLSourceLocation;

public interface GQLDeclaration {

  String description();

  <R> R apply(GQLDeclarationVisitor<R> visitor);

  List<GQLDirective> directives();

  GQLSourceLocation location();

}
