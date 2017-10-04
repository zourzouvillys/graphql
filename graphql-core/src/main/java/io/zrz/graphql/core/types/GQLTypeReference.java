package io.zrz.graphql.core.types;

import io.zrz.graphql.core.lang.GQLTypeVisitor;

/**
 * Used as a type for any field which represents a type which can be modified (non null, list, or scalar type).
 * 
 * @author theo
 *
 */

public interface GQLTypeReference
{

  <R> R apply(GQLTypeVisitor<R> visitor);

}
