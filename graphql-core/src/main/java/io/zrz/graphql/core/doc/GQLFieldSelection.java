package io.zrz.graphql.core.doc;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

/**
 * A single field selection, which can have arguments, directives, and sub selections.
 *
 * @author theo
 *
 */

@Value.Immutable(copy = true)
public abstract class GQLFieldSelection implements GQLSelection {

  public abstract String name();

  public abstract @Nullable String alias();

  public String outputName() {
    if (this.alias() == null) {
      return this.name();
    }
    return this.alias();
  }

  public abstract List<GQLArgument> args();

  public abstract List<GQLDirective> directives();

  public abstract List<GQLSelection> selections();

  public abstract GQLFieldSelection withDirectives(GQLDirective... value);

  public abstract GQLFieldSelection withArgs(GQLArgument... value);

  @Override
  public <R> R apply(GQLSelectionVisitor<R> visitor) {
    return visitor.visitFieldSelection(this);
  }

  public static GQLFieldSelection fieldSelection(final String name) {
    return builder().name(name).build();
  }

  public GQLArgument args(String name) {
    return this.args().stream().filter(a -> a.name().equals(name)).findAny().orElse(null);
  }

  public static ImmutableGQLFieldSelection.Builder builder() {
    return ImmutableGQLFieldSelection.builder();
  }

}
