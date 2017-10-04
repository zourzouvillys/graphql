package io.zrz.graphql.core.lang;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.types.GQLDeclarationRef;

public class PostCreationValidator implements GQLTypeDeclarationVisitor<Void> {

  private final GQLTypeRegistry reg;

  public PostCreationValidator(GQLTypeRegistry reg) {
    this.reg = reg;
  }

  @Override
  public Void visitUnion(GQLUnionTypeDeclaration type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitScalar(GQLScalarTypeDeclaration type) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Any object on input type decl must be an input or scalar type.
   */

  @Override
  public Void visitObject(GQLObjectTypeDeclaration type) {

    type.fields().forEach(field -> {

      field.args().stream().forEach(arg -> {

        if (!arg.type().apply(new OnlyReferenceInputTypes(this.reg))) {

          final GQLTypeDeclaration x = this.reg.resolve((GQLDeclarationRef) arg.type().apply(GQLTypeVisitors.rootType()));

          System.err.println(x);

          final StringBuilder sb = new StringBuilder();

          sb
              .append(arg.name())
              .append(" of field '")
              .append(field.name())
              .append("' on ")
              .append(type.name())
              .append(" must be scalar or input type, but was '")
              .append(x)
              .append("'");

          throw new RuntimeException(sb.toString());
        }

      });

    });

    return null;
  }

  /**
   * make sure that all the input types only have fields which reference scalars
   * or other input types.
   *
   * @param type
   * @return
   */

  @Override
  public Void visitInput(GQLInputTypeDeclaration type) {

    type.fields().forEach(field -> {

      if (!field.type().apply(new OnlyReferenceInputTypes(this.reg))) {
        throw new RuntimeException(
            String.format("%s on %s can only be a scalar or input type", field.name(), type.name()));
      }

    });
    return null;

    // throw new RuntimeException(String.format("'%s' can only refer to
    // scalars and other input types. not: '%s'", this.type.name(),
    // ref.name()));

  }

  /**
   *
   */

  @Override
  public Void visitInterface(GQLInterfaceTypeDeclaration type) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Void visitEnum(GQLEnumDeclaration type) {
    // TODO Auto-generated method stub
    return null;
  }

}
