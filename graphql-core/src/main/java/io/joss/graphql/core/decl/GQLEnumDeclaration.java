package io.joss.graphql.core.decl;

import java.util.List;

import io.joss.graphql.core.doc.GQLDirective;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder(builderClassName = "Builder")
public final class GQLEnumDeclaration implements GQLExtendableTypeDeclaration {

  private final String name;
  private final String description;
  private final boolean extension;

  @Singular
  private final List<GQLEnumValue> values;

  @Singular
  private final List<GQLDirective> directives;

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String description() {
    return this.description;
  }

  public List<GQLEnumValue> values() {
    return this.values;
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitEnum(this);
  }

  @Override
  public boolean isExtension() {
    return this.extension;
  }

  @Override
  public List<GQLDirective> directives() {
    return this.directives;
  }

}
