package io.zrz.graphql.core.decl;

import java.util.List;

import org.immutables.value.Value;

import io.zrz.graphql.core.types.GQLDeclarationRef;
import io.zrz.graphql.core.types.GQLTypeDeclKind;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public abstract class GQLInterfaceTypeDeclaration implements GQLExtendableTypeDeclaration {

  public abstract List<GQLDeclarationRef> ifaces();

  public abstract List<GQLParameterableFieldDeclaration> fields();

  @Override
  public GQLTypeDeclKind typeKind() {
    return GQLTypeDeclKind.INTERFACE;
  }

  public GQLParameterableFieldDeclaration field(final String name) {
    return this.fields().stream().filter(d -> d.name().equals(name)).findAny().orElse(null);
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitInterface(this);
  }

  public static class Builder extends ImmutableGQLInterfaceTypeDeclaration.Builder {

  }

  public static GQLInterfaceTypeDeclaration.Builder builder() {
    return new Builder();
  }

}
