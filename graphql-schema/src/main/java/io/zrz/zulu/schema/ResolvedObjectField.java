package io.zrz.zulu.schema;

import com.google.common.collect.ImmutableList;

import io.zrz.graphql.core.decl.GQLParameterableFieldDeclaration;

public class ResolvedObjectField implements SchemaElement {

  private ResolvedObjectOrInterfaceType enclosing;
  private GQLParameterableFieldDeclaration fdecl;
  private TypeUse returnType;
  private ImmutableList<ObjectFieldArgument> params;

  public ResolvedObjectField(ResolvedObjectOrInterfaceType enclosing, GQLParameterableFieldDeclaration fdecl, SchemaCompiler c) {
    this.enclosing = enclosing;
    this.fdecl = fdecl;
    this.returnType = c.use(this, fdecl.type());
    this.params = fdecl.args()
        .stream()
        .map(arg -> new ObjectFieldArgument(this, arg, c.use(this, arg.type())))
        .collect(ImmutableList.toImmutableList());
  }

  @Override
  public ResolvedSchema schema() {
    return this.enclosing.schema();
  }

  public ResolvedObjectOrInterfaceType enclosingType() {
    return this.enclosing;
  }

  public TypeRef returnType() {
    return this.returnType;
  }

  public ImmutableList<ObjectFieldArgument> parameters() {
    return this.params;
  }

  public String fieldName() {
    return fdecl.name();
  }

}
