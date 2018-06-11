package io.zrz.graphql.core.decl;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;

@Value.Immutable(copy = true)
public interface GQLEnumValue {

  String name();

  @Nullable
  String description();

  @Nullable
  String deprecationReason();

  List<GQLDirective> directives();

  public static ImmutableGQLEnumValue.Builder builder() {
    return ImmutableGQLEnumValue.builder();
  }

}
