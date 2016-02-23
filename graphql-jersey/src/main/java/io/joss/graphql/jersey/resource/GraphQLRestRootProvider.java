package io.joss.graphql.jersey.resource;

import io.joss.graphql.core.doc.GQLSelectedOperation;
import io.joss.graphql.core.value.GQLObjectValue;

@FunctionalInterface
public interface GraphQLRestRootProvider
{

  GQLObjectValue execute(GraphQLHttpParams http, GQLSelectedOperation query, GQLObjectValue input);

}
