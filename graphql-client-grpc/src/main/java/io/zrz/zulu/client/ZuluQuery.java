package io.zrz.zulu.client;

/**
 * an operation which has no side effect on the server side, and may potentially be locally cached depending on the
 * configuration.
 * 
 * @author theo
 *
 */

public interface ZuluQuery<T> extends ZuluOperation<T> {

}
