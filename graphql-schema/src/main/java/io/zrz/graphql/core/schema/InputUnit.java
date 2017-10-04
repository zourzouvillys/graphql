package io.zrz.graphql.core.schema;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import io.zrz.graphql.core.decl.GQLDeclaration;
import io.zrz.graphql.core.decl.GQLInputTypeDeclaration;
import io.zrz.graphql.core.decl.GQLSchemaDeclaration;
import io.zrz.graphql.core.decl.GQLTypeDeclaration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Singular;

@Data
@AllArgsConstructor
public class InputUnit {

  private String source;

  @Singular
  private List<GQLDeclaration> decls;

  public <T extends GQLDeclaration> List<T> ofType(Class<T> type) {
    return this.decls.stream()
        .filter(decl -> type.isAssignableFrom(decl.getClass()))
        .map(in -> type.cast(in))
        .collect(Collectors.toList());
  }

  public List<GQLInputTypeDeclaration> inputTypes() {
    return this.ofType(GQLInputTypeDeclaration.class);
  }

  public List<GQLTypeDeclaration> typeDecls() {
    return this.ofType(GQLTypeDeclaration.class);
  }

  public List<GQLSchemaDeclaration> schemas() {
    return this.ofType(GQLSchemaDeclaration.class);
  }

  /**
   * set of all declared type symbols in this unit
   */

  public Set<String> declaredSymbols() {
    return this.typeDecls().stream()
        .map(in -> in.name())
        .distinct()
        .collect(Collectors.toSet());
  }

  /**
   * returns all declaration fragments for the given symbol.
   */

  public Set<GQLTypeDeclaration> decls(String name) {
    return this.typeDecls().stream()
        .filter(in -> in.name().equals(name))
        .collect(Collectors.toSet());
  }

  /**
   * set of all declared type symbols in this unit
   */

  public Map<String, Set<GQLTypeDeclaration>> types() {
    return this.declaredSymbols().stream()
        .map(name -> Maps.immutableEntry(name, this.decls(name)))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

}
