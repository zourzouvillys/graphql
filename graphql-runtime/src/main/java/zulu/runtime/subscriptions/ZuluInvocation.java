package zulu.runtime.subscriptions;

/**
 * an invocation is a executable bound and resolved with all variables and handlers satisfied. the only thing that
 * remains to be done is actually perform the execution and get the results.
 *
 * this is provided through the API so that execution can be performed in different ways - subscriptions use a flow
 * controlled mechanism, and normal queries can, but may also just return the results directly.
 *
 * @author theo
 *
 */

public interface ZuluInvocation {

}
