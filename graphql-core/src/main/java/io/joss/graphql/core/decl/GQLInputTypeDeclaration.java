package io.joss.graphql.core.decl;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@ToString
@Wither
@Builder(builderClassName = "Builder")
public class GQLInputTypeDeclaration implements GQLExtendableTypeDeclaration {

  private final String name;
  private final String description;
  private final boolean extension;

  @Singular
  private final List<GQLInputFieldDeclaration> fields;

  public static class Builder {
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String description() {
    return this.description;
  }

  public List<GQLInputFieldDeclaration> fields() {
    return this.fields;
  }

  @Override
  public <R> R apply(GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitInput(this);
  }

  @Override
  public boolean isExtension() {
    return this.extension;
  }

}
