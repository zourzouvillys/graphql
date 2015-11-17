package io.joss.graphql.core.binder.runtime;

import io.joss.graphql.core.decl.GQLDeclaration;
import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.lang.GQLTypeRegistry;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataContexts
{

  /**
   * Builds a data context based on a given root and query.
   * 
   * The data context has no awareness of specific executions - e.g, the caller, it's arguments, etc. This is to allow queries to be shared
   * without needing to rebuild them every time, as all instances of the same client (and version) will use the same query set most of the
   * time.
   * 
   * @param registry
   * @param root
   * @param op
   * @return
   */

  public static DataContext build(GQLTypeRegistry registry, GQLDeclaration root, GQLSelectedOperation op)
  {
    DataContext parent = new DataContext(registry, root, op.doc());
    op.operation().selections().forEach(s -> s.apply(parent));
    return parent;
  }

}
