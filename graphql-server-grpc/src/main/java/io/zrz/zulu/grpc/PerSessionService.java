package io.zrz.zulu.grpc;

import java.util.Collection;
import java.util.UUID;

import com.google.common.base.Supplier;

import io.grpc.Attributes;
import io.grpc.BindableService;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerMethodDefinition;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;

public class PerSessionService implements BindableService {

  private ServerServiceDefinition perSessionBinding;

  public PerSessionService(ServiceDescriptor desc) {
    perSessionBinding = bindFactory(desc);
  }

  @Override
  public ServerServiceDefinition bindService() {
    return perSessionBinding;
  }

  private ServerServiceDefinition bindFactory(ServiceDescriptor descriptor) {
    Collection<MethodDescriptor<?, ?>> methods = descriptor.getMethods();
    ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(descriptor);
    methods.forEach(method -> builder.addMethod(ServerMethodDefinition.create(method, new PerSessionServerCallHandler())));
    return builder.build();
  }

  private class PerSessionServerCallHandler implements ServerCallHandler {

    PerSessionServerCallHandler() {
    }

    @Override
    @SuppressWarnings("unchecked")

    public ServerCall.Listener<?> startCall(ServerCall call, Metadata headers) {

      PeerContext sessionId = call.getAttributes().get(BindingServerTransportFilter.PEER_CONTEXT);

      if (sessionId == null) {
        throw new IllegalStateException("no peer context");
      }

      BindableService instance = sessionId.getServiceInstance();

      if (instance == null) {
        throw new IllegalStateException("no service instance");
      }
      
      return sessionId.startCall(call, headers);


    }

  }

}
