package io.zrz.graphql.zulu.runtime;

import io.zrz.graphql.core.doc.GQLVariableDefinition;
import io.zrz.graphql.core.lang.GQLTypeVisitor;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;
import io.zrz.graphql.core.value.GQLVariableRef;
import io.zrz.zulu.types.ZTypeUse;
import io.zrz.zulu.values.ZValue;
import io.zrz.zulu.values.ZValueProvider;
import io.zrz.zulu.values.ZValues;

class VarRefValueProvider implements ZValueProvider {

  private GQLVariableDefinition var;
  private GQLVariableRef ref;
  private GQLTypeResolver resolver;

  public VarRefValueProvider(GQLTypeResolver resolver, GQLVariableRef ref, GQLVariableDefinition var) {
    this.resolver = resolver;
    this.ref = ref;
    this.var = var;
  }

  public static ZValueProvider ofVar(GQLTypeResolver resolver, GQLVariableRef ref, GQLVariableDefinition var) {
    return new VarRefValueProvider(resolver, ref, var);
  }

  @Override
  public ZTypeUse type() {

    return var.type().apply(new GQLTypeVisitor<ZTypeUse>() {

      @Override
      public ZTypeUse visitDeclarationRef(GQLDeclarationRef arg0) {
        // TODO: attach directives
        return ZTypeUse.of(resolver.resolve(arg0.name()));
      }

      @Override
      public ZTypeUse visitList(GQLListType arg0) {
        // TODO: attach directives
        return arg0.type().apply(this);
      }

      @Override
      public ZTypeUse visitNonNull(GQLNonNullType arg0) {
        // TODO: attach directives & nullability
        return arg0.type().apply(this);
      }

    });

  }

  @Override
  public ZValue resolve() {
    return ZValues.of("???");
  }

  @Override
  public String toString() {
    return "$" + var.name() + "(" + var.type() + var.directives() + ")";
  }

}
