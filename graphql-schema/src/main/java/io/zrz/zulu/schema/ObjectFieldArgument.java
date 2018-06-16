package io.zrz.zulu.schema;

import io.zrz.graphql.core.decl.GQLArgumentDefinition;

public class ObjectFieldArgument {

  private ResolvedObjectField field;
  private GQLArgumentDefinition arg;
  private TypeUse type;

  public ObjectFieldArgument(ResolvedObjectField field, GQLArgumentDefinition arg, TypeUse use) {
    this.field = field;
    this.arg = arg;
    this.type = use;
  }

  public String name() {
    return arg.name();
  }

  public TypeUse type() {
    return type;
  }

}
