package io.zrz.graphql.core.lang;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.zrz.graphql.core.decl.GQLArgumentDefinition;
import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.decl.GQLInputFieldDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.zrz.graphql.core.decl.GQLObjectTypeDeclaration;
import io.zrz.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.zrz.graphql.core.decl.GQLScalarTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclarationVisitor;
import io.zrz.graphql.core.decl.GQLUnionTypeDeclaration;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLListType;
import io.zrz.graphql.core.types.GQLNonNullType;
import io.zrz.graphql.core.types.GQLTypeReference;

public class ResolveReferencesVisitor implements GQLTypeDeclarationVisitor<GQLTypeDeclaration>, GQLTypeVisitor<GQLTypeReference> {

  private final Map<String, GQLTypeDeclaration> types;

  public ResolveReferencesVisitor(Map<String, GQLTypeDeclaration> types) {
    this.types = types;
  }

  private GQLTypeReference replace(GQLTypeReference ref) {
    return ref.apply(this);
  }

  private GQLArgumentDefinition replace(GQLArgumentDefinition ref) {
    return ref.withType(this.replace(ref.type()));
  }

  private List<GQLDeclarationRef> replace(List<GQLDeclarationRef> type, String message) {
    try {
      return type.stream().map(ref -> ref.withRef(this.resolve(ref.name()))).collect(Collectors.toList());
    } catch (final Exception ex) {
      throw new RuntimeException(message, ex);
    }
  }

  private List<GQLParameterableFieldDeclaration> replaceFields(List<GQLParameterableFieldDeclaration> fields) {
    return fields.stream().map(field -> this.updateField(field)).collect(Collectors.toList());
  }

  private GQLParameterableFieldDeclaration updateField(GQLParameterableFieldDeclaration field) {
    try {
      return field
          .withType(this.replace(field.type()))
          .withArgs(this.replaceArgs(field.args()));
    } catch (final Exception ex) {
      throw new RuntimeException(String.format("on field '%s'", field.name()), ex);
    }
  }

  private List<GQLInputFieldDeclaration> replaceInputFields(List<GQLInputFieldDeclaration> fields) {
    return fields.stream().map(field -> field.withType(this.replace(field.type()))).collect(Collectors.toList());
  }

  private List<GQLArgumentDefinition> replaceArgs(List<GQLArgumentDefinition> args) {
    return args.stream().map(arg -> this.replace(arg)).collect(Collectors.toList());
  }

  @Override
  public GQLTypeDeclaration visitUnion(GQLUnionTypeDeclaration type) {
    return type.withTypes(this.replace(type.types(), "in union type"));
  }

  @Override
  public GQLTypeDeclaration visitScalar(GQLScalarTypeDeclaration type) {
    return type;
  }

  @Override
  public GQLTypeDeclaration visitInput(GQLInputTypeDeclaration type) {
    return type
        .withFields(this.replaceInputFields(type.fields()));
  }

  @Override
  public GQLTypeDeclaration visitObject(GQLObjectTypeDeclaration type) {
    try {
      return type
          .withIfaces(this.replace(type.ifaces(), "in interface"))
          .withFields(this.replaceFields(type.fields()));
    } catch (final Exception ex) {
      throw new RuntimeException(String.format("in type %s", type.name()), ex);
    }
  }

  @Override
  public GQLTypeDeclaration visitInterface(GQLInterfaceTypeDeclaration type) {
    try {
      return type
          .withIfaces(this.replace(type.ifaces(), "in interface"))
          .withFields(this.replaceFields(type.fields()));
    } catch (final Exception ex) {
      throw new RuntimeException(String.format("in interface %s", type.name()), ex);
    }
  }

  @Override
  public GQLTypeDeclaration visitEnum(GQLEnumDeclaration type) {
    return type;
  }

  // ---

  @Override
  public GQLTypeReference visitNonNull(GQLNonNullType type) {
    return type.withWrappedType(type.type().apply(this));
  }

  @Override
  public GQLTypeReference visitList(GQLListType type) {
    return type.withWrappedType(type.type().apply(this));
  }

  @Override
  public GQLDeclarationRef visitDeclarationRef(GQLDeclarationRef type) {
    final GQLTypeDeclaration ref = this.types.get(type.name());
    if (ref == null) {
      throw new UnresolvableTypeNameException(type.name());
    }
    return type.withRef(this.resolve(type.name()));
  }

  private GQLTypeDeclaration resolve(String name) {
    final GQLTypeDeclaration replacement = this.types.get(name);
    if (replacement == null) {
      throw new UnresolvableTypeNameException(name);
    }
    return replacement;
  }

}
