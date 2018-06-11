package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLTypeReference;

@Value.Immutable(copy = true)
public abstract class GQLObjectTypeDeclaration implements GQLExtendableTypeDeclaration {

  public static class Builder extends ImmutableGQLObjectTypeDeclaration.Builder {

    public Builder addField(final String name, final GQLTypeReference type) {
      return this.addFields(GQLParameterableFieldDeclaration.builder().name(name).type(type).build());
    }

  }

  public abstract List<GQLParameterableFieldDeclaration> fields();

  public abstract List<GQLDeclarationRef> ifaces();

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitObject(this);
  }

  public GQLParameterableFieldDeclaration field(String name) {
    return this.fields().stream().filter(d -> d.name().equals(name)).findAny().orElse(null);
  }

  // public abstract GQLObjectTypeDeclaration withIfaces(GQLDeclarationRef value);

  public static Builder builder() {
    return new Builder();
  }

}
