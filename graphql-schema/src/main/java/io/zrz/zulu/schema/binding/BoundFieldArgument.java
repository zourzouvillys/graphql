package io.zrz.zulu.schema.binding;

import io.zrz.graphql.core.doc.GQLArgument;
import io.zrz.zulu.schema.ObjectFieldArgument;
import io.zrz.zulu.schema.ResolvedType;
import io.zrz.zulu.schema.TypeUse;

public class BoundFieldArgument {

  private BoundObjectSelection sel;
  private ObjectFieldArgument param;
  private GQLArgument arg;

  public BoundFieldArgument(BoundObjectSelection sel, ObjectFieldArgument param, GQLArgument arg) {
    this.sel = sel;
    this.param = param;
    this.arg = arg;
  }

  public String name() {
    return this.param.name();
  }

  public ResolvedType type() {
    return param.type().targetType();
  }

  public TypeUse typeUse() {
    return param.type();
  }

}
