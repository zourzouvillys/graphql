package io.zrz.graphql.core.decl;

import java.util.List;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@EqualsAndHashCode
@Wither
@ToString
@Builder(builderClassName = "Builder")
public final class GQLUnionTypeDeclaration implements GQLExtendableTypeDeclaration {

  private final String name;
  private final String description;
  private final boolean extension;

  @Singular
  private final List<GQLDeclarationRef> types;

  @Singular
  private final List<GQLDirective> directives;

  private final GQLSourceLocation location;

  @Override
  public GQLSourceLocation location() {
    return this.location;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String description() {
    return this.description;
  }

  public List<GQLDeclarationRef> types() {
    return this.types;
  }

  @Override
  public <R> R apply(final GQLTypeDeclarationVisitor<R> visitor) {
    return visitor.visitUnion(this);
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
