package io.zrz.graphql.core.decl;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.parser.GQLSourceLocation;

public interface GQLDeclaration {

  @Nullable
  String description();

  <R> R apply(GQLDeclarationVisitor<R> visitor);

  List<GQLDirective> directives();

  @Nullable
  GQLSourceLocation location();

  GQLDeclaration withDescription(String value);

  GQLDeclaration withDirectives(GQLDirective... elements);

  GQLDeclaration withDirectives(Iterable<? extends GQLDirective> elements);

  GQLDeclaration withLocation(GQLSourceLocation value);

}
