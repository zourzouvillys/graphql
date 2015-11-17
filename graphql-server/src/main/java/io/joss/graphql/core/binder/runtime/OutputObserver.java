package io.joss.graphql.core.binder.runtime;

/**
 * Used to provide output values as we recurse a tree.
 * 
 * @author theo
 *
 * @param <ParentT>
 * @param <ChildT>
 */

public interface OutputObserver<ParentT, ChildT>
{

  void onNext(ParentT parent, ChildT child);

  void onComplete();

}
