package io.zrz.graphql.core.doc;

import java.util.List;

import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.core.types.GQLDeclarationRef;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Wither;

@Wither
@EqualsAndHashCode
@ToString
@Builder
public final class GQLInlineFragmentSelection implements GQLSelection {

  private final GQLDeclarationRef typeCondition;
  private final GQLSourceLocation location;

  @Singular
  private final List<GQLDirective> directives;

  @Singular
  private final List<GQLSelection> selections;

  public GQLDeclarationRef typeCondition() {
    return this.typeCondition;
  }

  public List<GQLDirective> directives() {
    return this.directives;
  }

  public List<GQLSelection> selections() {
    return this.selections;
  }

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitInlineFragment(this);
  }

  @Override
  public GQLSourceLocation location() {
    return this.location;
  }

}
