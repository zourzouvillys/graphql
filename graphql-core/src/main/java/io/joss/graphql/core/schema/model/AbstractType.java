package io.joss.graphql.core.schema.model;

abstract class AbstractType implements Type {

  protected final Model model;
  private final String name;

  protected AbstractType(TypeBuilder typebuilder, Model model, String name) {
    typebuilder.register(this, name);
    this.model = model;
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

}
