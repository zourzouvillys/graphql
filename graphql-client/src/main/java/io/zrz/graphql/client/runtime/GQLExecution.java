package io.zrz.graphql.client.runtime;

import io.zrz.graphql.core.value.GQLObjectValue;

/**
 * Models a single execution to the server, along with it's result set when available.
 */

public interface GQLExecution
{

  GQLObjectValue get();


}
