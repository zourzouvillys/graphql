package io.zrz.graphql.jersey.resource;

import io.zrz.graphql.core.doc.GQLSelectedOperation;
import io.zrz.graphql.core.value.GQLObjectValue;

@FunctionalInterface
public interface GraphQLRestRootProvider
{

  GQLObjectValue execute(GraphQLHttpParams http, GQLSelectedOperation query, GQLObjectValue input);

}
