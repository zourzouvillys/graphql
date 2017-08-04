package io.joss.graphql.core.schema.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.doc.GQLDirective;
import io.joss.graphql.core.parser.GQLSourceLocation;

abstract class AbstractType implements Type {

  protected final Model model;
  private final String name;
  private final List<GQLDirective> directives;
  private final GQLSourceLocation location;

  protected AbstractType(TypeBuilder typebuilder, Model model, String name, GQLDeclaration decl, Collection<? extends GQLDeclaration> exts) {
    typebuilder.register(this, name);
    this.model = model;
    this.name = name;
    this.location = decl.location();
    this.directives = decl.directives() == null ? Collections.emptyList() : Collections.unmodifiableList(decl.directives());
  }

  public AbstractType(TypeBuilder typebuilder, Model model, String name, GQLDeclaration decl) {
    this(typebuilder, model, name, decl, Collections.emptyList());
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public List<GQLDirective> getDirectives() {
    return this.directives;
  }

  @Override
  public GQLSourceLocation getLocation() {
    return this.location;
  }

}
