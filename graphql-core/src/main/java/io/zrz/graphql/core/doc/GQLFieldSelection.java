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
@Value.Style(allowedClasspathAnnotations = { Override.class })
public interface GQLFieldSelection extends GQLSelection {

  String name();

  @Nullable
  String alias();

  default String outputName() {
    if (this.alias() == null) {
      return this.name();
    }
    return this.alias();
  }

  List<GQLArgument> args();

  List<GQLDirective> directives();

  List<GQLSelection> selections();

  GQLFieldSelection withDirectives(GQLDirective... value);

  GQLFieldSelection withArgs(GQLArgument... value);

  @Override
  default GQLSelectionKind selectionKind() {
    return GQLSelectionKind.FIELD;
  }

  @Override
  default <R> R apply(final GQLSelectionVisitor<R> visitor) {
    return visitor.visitFieldSelection(this);
  }

  @Override
  default <T, R> R apply(final FunctionVisitor<T, R> visitor, final T value) {
    return visitor.visitFieldSelection(this, value);
  }

  @Override
  default <T> void apply(final ConsumerVisitor<T> visitor, final T value) {
    visitor.visitFieldSelection(this, value);
  }

  @Override
  default void apply(final VoidVisitor visitor) {
    visitor.visitFieldSelection(this);
  }

  default GQLArgument args(final String name) {
    return this.args().stream().filter(a -> a.name().equals(name)).findAny().orElse(null);
  }

  static GQLFieldSelection fieldSelection(final String name) {
    return builder().name(name).build();
  }

  static ImmutableGQLFieldSelection.Builder builder() {
    return ImmutableGQLFieldSelection.builder();
  }

}
