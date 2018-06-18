package io.zrz.zulu.schema.binding;

import org.eclipse.jdt.annotation.Nullable;

import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.value.GQLValue;
import io.zrz.zulu.schema.TypeUse;

public class BoundVariable {

  private String name;
  private TypeUse type;
  private @Nullable GQLValue defaultValue;

  public BoundVariable(BoundOperation op, GQLVariableDefinition var, BoundBuilder b) {
    this.name = var.name();
    this.type = b.resolve(var.type());
    this.defaultValue = var.defaultValue();
  }

  public String name() {
    return name;
  }

  public TypeUse type() {
    return this.type;
  }

  public @Nullable GQLValue defaultValue() {
    return this.defaultValue;
  }

}
