package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLTypeDeclKind;
import io.zrz.graphql.core.types.GQLTypeReference;

@Value.Immutable(copy = true)
public abstract class GQLInterfaceTypeDeclaration implements GQLExtendableTypeDeclaration {

  public abstract List<GQLDeclarationRef> ifaces();

  public abstract List<GQLParameterableFieldDeclaration> fields();

  @Override
  public GQLTypeDeclKind typeKind() {
    return GQLTypeDeclKind.INTERFACE;
  }

  public GQLParameterableFieldDeclaration field(String name) {
    return this.fields().stream().filter(d -> d.name().equals(name)).findAny().orElse(null);
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitInterface(this);
  }

  public static class Builder extends ImmutableGQLInterfaceTypeDeclaration.Builder {

    public Builder addField(final String name, final GQLTypeReference type) {
      return super.addFields(GQLParameterableFieldDeclaration.builder().name(name).type(type).build());
    }

  }

  public static Builder builder() {
    return new Builder();
  }

}
