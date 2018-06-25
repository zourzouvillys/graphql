package io.zrz.zulu.grpc;

import java.util.function.Supplier;

import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;

public class PeerContext {

  private BindableService instance;
  private volatile ServerServiceDefinition definition;

  public PeerContext(Supplier<BindableService> factory) {
    this.instance = factory.get();

  }

  public BindableService getServiceInstance() {
    return this.instance;
  }

  public Listener<?> startCall(ServerCall call, Metadata headers) {
    if (this.definition == null) {
      synchronized (this) {
        if (this.definition == null) {
          this.definition = instance.bindService();
        }
      }
    }
    ServerMethodDefinition<?, ?> method = definition.getMethod(call.getMethodDescriptor().getFullMethodName());
    return method.getServerCallHandler().startCall(call, headers);
  }

}
