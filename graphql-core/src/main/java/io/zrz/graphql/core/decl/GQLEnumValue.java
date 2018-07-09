package io.zrz.graphql.core.decl;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import io.zrz.graphql.core.doc.GQLDirective;

@Value.Immutable(copy = true)
@Value.Style(allowedClasspathAnnotations = { Override.class })
public interface GQLEnumValue {

  String name();

  @Nullable
  String description();

  @Nullable
  String deprecationReason();

  List<GQLDirective> directives();

  static ImmutableGQLEnumValue.Builder builder() {
    return ImmutableGQLEnumValue.builder();
  }

}
