package io.zrz.graphql.core.schema.model;

import java.util.Collection;
import java.util.stream.Collectors;

import io.zrz.graphql.core.doc.GQLDirective;
import io.zrz.graphql.core.parser.GQLSourceLocation;
import io.zrz.graphql.core.schema.TypeVisitors;

/**
 * base type for input, type, enum, union and scalar.
 *
 * @author theo
 *
 */

public interface Type {

  String getName();

  Collection<GQLDirective> getDirectives();

  default Collection<GQLDirective> getDirectives(String name) {
    return getDirectives().stream().filter(d -> d.name().equals(name)).collect(Collectors.toList());
  }

  <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor);

  void apply(TypeVisitors.NoReturnVisitor visitor);

  default boolean hasDirective(String name) {
    return getDirectives().stream().filter(d -> d.name().equals(name)).findAny().isPresent();
  }

  GQLSourceLocation getLocation();

}
