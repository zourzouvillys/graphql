package io.joss.graphql.core.lang;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.joss.graphql.core.decl.GQLArgumentDefinition;
import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.decl.GQLDeclarationVisitor;
import io.joss.graphql.core.decl.GQLEnumDeclaration;
import io.joss.graphql.core.decl.GQLInputFieldDeclaration;
import io.joss.graphql.core.decl.GQLInputTypeDeclaration;
import io.joss.graphql.core.decl.GQLInterfaceTypeDeclaration;
import io.joss.graphql.core.decl.GQLObjectTypeDeclaration;
import io.joss.graphql.core.decl.GQLParameterableFieldDeclaration;
import io.joss.graphql.core.decl.GQLScalarTypeDeclaration;
import io.joss.graphql.core.decl.GQLUnionTypeDeclaration;
import io.joss.graphql.core.types.GQLDeclarationRef;
import io.joss.graphql.core.types.GQLListType;
import io.joss.graphql.core.types.GQLNonNullType;
import io.joss.graphql.core.types.GQLTypeReference;

public class ResolveReferencesVisitor implements GQLDeclarationVisitor<GQLDeclaration>, GQLTypeVisitor<GQLTypeReference>
{

  private Map<String, GQLDeclaration> types;

  public ResolveReferencesVisitor(Map<String, GQLDeclaration> types)
  {
    this.types = types;
  }

  private GQLTypeReference replace(GQLTypeReference ref)
  {
    return ref.apply(this);
  }

  private GQLArgumentDefinition replace(GQLArgumentDefinition ref)
  {
    return ref.withType(this.replace(ref.type()));
  }

  private List<GQLDeclarationRef> replace(List<GQLDeclarationRef> type, String message)
  {
    try
    {
      return type.stream().map(ref -> ref.withRef(resolve(ref.name()))).collect(Collectors.toList());
    }
    catch (Exception ex)
    {
      throw new RuntimeException(message, ex);
    }
  }

  private List<GQLParameterableFieldDeclaration> replaceFields(List<GQLParameterableFieldDeclaration> fields)
  {
    return fields.stream().map(field -> updateField(field)).collect(Collectors.toList());
  }

  private GQLParameterableFieldDeclaration updateField(GQLParameterableFieldDeclaration field)
  {
    try
    {
      return field
          .withType(replace(field.type()))
          .withArgs(replaceArgs(field.args()));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("on field '%s'", field.name()), ex);
    }
  }

  private List<GQLInputFieldDeclaration> replaceInputFields(List<GQLInputFieldDeclaration> fields)
  {
    return fields.stream().map(field -> field.withType(replace(field.type()))).collect(Collectors.toList());
  }

  private List<GQLArgumentDefinition> replaceArgs(List<GQLArgumentDefinition> args)
  {
    return args.stream().map(arg -> replace(arg)).collect(Collectors.toList());
  }

  @Override
  public GQLDeclaration visitUnion(GQLUnionTypeDeclaration type)
  {
    return type.withTypes(replace(type.types(), "in union type"));
  }

  @Override
  public GQLDeclaration visitScalar(GQLScalarTypeDeclaration type)
  {
    return type;
  }

  @Override
  public GQLDeclaration visitInput(GQLInputTypeDeclaration type)
  {
    return type
        .withFields(replaceInputFields(type.fields()));
  }

  @Override
  public GQLDeclaration visitObject(GQLObjectTypeDeclaration type)
  {
    try
    {
      return type
          .withIfaces(replace(type.ifaces(), "in interface"))
          .withFields(replaceFields(type.fields()));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("in type %s", type.name()), ex);
    }
  }

  @Override
  public GQLDeclaration visitInterface(GQLInterfaceTypeDeclaration type)
  {
    try
    {
      return type
          .withIfaces(replace(type.ifaces(), "in interface"))
          .withFields(replaceFields(type.fields()));
    }
    catch (Exception ex)
    {
      throw new RuntimeException(String.format("in interface %s", type.name()), ex);
    }
  }

  @Override
  public GQLDeclaration visitEnum(GQLEnumDeclaration type)
  {
    return type;
  }

  // ---

  @Override
  public GQLTypeReference visitNonNull(GQLNonNullType type)
  {
    return type.withWrappedType(type.type().apply(this));
  }

  @Override
  public GQLTypeReference visitList(GQLListType type)
  {
    return type.withWrappedType(type.type().apply(this));
  }

  @Override
  public GQLDeclarationRef visitDeclarationRef(GQLDeclarationRef type)
  {
    GQLDeclaration ref = types.get(type.name());
    if (ref == null)
    {
      throw new UnresolvableTypeNameException(type.name());
    }
    return type.withRef(resolve(type.name()));
  }

  private GQLDeclaration resolve(String name)
  {
    GQLDeclaration replacement = types.get(name);
    if (replacement == null)
    {
      throw new UnresolvableTypeNameException(name);
    }
    return replacement;
  }

}
