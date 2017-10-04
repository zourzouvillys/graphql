package io.zrz.graphql.core.schema.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import io.zrz.graphql.core.decl.GQLEnumDeclaration;
import io.zrz.graphql.core.schema.TypeVisitors;
import io.zrz.graphql.core.schema.TypeVisitors.NoReturnVisitor;

public class EnumType extends AbstractType implements InputCompatibleType {

  private final List<String> values;

  protected EnumType(TypeBuilder typebuilder, Model model, String name, GQLEnumDeclaration decl, Collection<GQLEnumDeclaration> exts) {
    super(typebuilder, model, name, decl, exts);
    this.values = Streams.concat(Stream.of(decl), exts.stream()).flatMap(e -> e.values().stream()).map(v -> v.name()).collect(Collectors.toList());
  }

  public List<String> enumValues() {
    return this.values;
  }

  @Override
  public <R> R apply(TypeVisitors.GenericReturnVisitor<R> visitor) {
    return visitor.visitEnumType(this);
  }

  @Override
  public void apply(NoReturnVisitor visitor) {
    visitor.visitEnumType(this);
  }

}
