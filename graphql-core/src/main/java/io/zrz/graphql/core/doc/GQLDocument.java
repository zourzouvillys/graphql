package io.zrz.graphql.core.doc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

@Value.Immutable(copy = true)
public abstract class GQLDocument {

  public abstract List<GQLDefinition> definitions();

  /**
   * finds the operation with the given name.
   *
   * @param name
   * @return
   */

  public GQLOperationDefinition named(String name) {
    return this.definitions()
        .stream()
        .map(def -> def.apply(GQLDefinitionVisitors.operationExtractor()))
        .filter(p -> p != null)
        .filter(val -> val.name() != null && name.equals(val.name()))
        .findAny()
        .orElse(null);
  }

  public GQLFragmentDefinition fragment(String name) {

    final GQLFragmentDefinition ret = this.definitions()
        .stream()
        .map(def -> def.apply(GQLDefinitionVisitors.fragmentExtractor()))
        .filter(p -> p != null)
        .filter(val -> val.name().equals(name))
        .findAny()
        .orElse(null);

    if (ret == null) {
      throw new IllegalStateException(String.format("Unknown fragment '%s'", name));
    }

    return ret;

  }

  public Collection<GQLOperationDefinition> operations() {
    return this.definitions()
        .stream()
        .map(def -> def.apply(GQLDefinitionVisitors.operationExtractor()))
        .filter(p -> p != null)
        .collect(Collectors.toList());
  }

  public Collection<GQLFragmentDefinition> fragments() {
    return this.definitions()
        .stream()
        .map(def -> def.apply(GQLDefinitionVisitors.fragmentExtractor()))
        .filter(p -> p != null)
        .collect(Collectors.toList());
  }

  public GQLSelectedOperation select(String namedQuery) {
    return GQLSelectedOperation.namedQuery(this, namedQuery);
  }

  public GQLSelectedOperation defaultOperation() {
    return GQLSelectedOperation.defaultQuery(this);
  }

  public static ImmutableGQLDocument.Builder builder() {
    return ImmutableGQLDocument.builder();
  }

}
