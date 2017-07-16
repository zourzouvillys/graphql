package io.joss.graphql.core.schema;

import java.util.Set;

import com.google.common.base.Preconditions;

import io.joss.graphql.core.decl.GQLTypeDeclaration;
import io.joss.graphql.core.schema.model.Type;

public class TypeUtils {

  /**
   * Given a list of decls, builds a type.
   *
   * @param name
   * @param decls
   * @return
   */

  public static Type build(String name, Set<GQLTypeDeclaration> decls) {
    Preconditions.checkArgument(!decls.isEmpty());
    final TypeBuilder builder = new TypeBuilder(name);
    decls.forEach(in -> in.apply(builder));
    return builder.build();
  }

}
