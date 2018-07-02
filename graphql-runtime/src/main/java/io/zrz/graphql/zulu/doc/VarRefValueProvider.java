package io.zrz.graphql.zulu.doc;

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

  private final GQLVariableDefinition var;
  private final GQLVariableRef ref;
  private final GQLTypeResolver resolver;

  public VarRefValueProvider(final GQLTypeResolver resolver, final GQLVariableRef ref, final GQLVariableDefinition var) {
    this.resolver = resolver;
    this.ref = ref;
    this.var = var;
  }

  public static ZValueProvider ofVar(final GQLTypeResolver resolver, final GQLVariableRef ref, final GQLVariableDefinition var) {
    return new VarRefValueProvider(resolver, ref, var);
  }

  @Override
  public ZTypeUse type() {

    return this.var.type().apply(new GQLTypeVisitor<ZTypeUse>() {

      @Override
      public ZTypeUse visitDeclarationRef(final GQLDeclarationRef arg0) {
        // TODO: attach directives

        return ZTypeUse.of(VarRefValueProvider.this.resolver.resolve(arg0.name()));
      }

      @Override
      public ZTypeUse visitList(final GQLListType arg0) {
        // TODO: attach directives
        return arg0.type().apply(this);
      }

      @Override
      public ZTypeUse visitNonNull(final GQLNonNullType arg0) {
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
    return "$" + this.var.name() + "(" + this.var.type() + this.var.directives() + ")";
  }

}
