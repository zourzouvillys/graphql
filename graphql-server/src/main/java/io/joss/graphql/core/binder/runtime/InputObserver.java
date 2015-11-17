package io.joss.graphql.core.binder.runtime;

/**
 * interface for returning multiple children from a single input request.
 */

public interface InputObserver<ValueT, ParentT>
{
  
  void onParent(ParentT parent, OutputObserver<ParentT, ValueT> output);

  void onCompleted();
  
}
