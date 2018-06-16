package io.zrz.graphql.zulu.engine;

import java.lang.invoke.MethodHandle;

/**
 * inputs are in JSON by default, but other formats are supported - e.g, BSON.
 * 
 * the reader needs to be able to convert from the input form into the expected form for execution.
 * 
 * {@link MethodHandle} instances are used for "pulling" the data out of the parameters. this allows us to bind prepared
 * queries to a fast path for execution.
 * 
 * 
 * @author theo
 *
 */

public interface ZuluParameterReader {

}
