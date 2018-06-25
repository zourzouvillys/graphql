package io.zrz.zulu.grpc;

import java.util.function.Supplier;

import io.grpc.Attributes;
import io.grpc.BindableService;
import io.grpc.ServerTransportFilter;

public class BindingServerTransportFilter extends ServerTransportFilter {

  public static final Attributes.Key<PeerContext> PEER_CONTEXT = Attributes.Key.of("PEER_CONTEXT");
  private Supplier<BindableService> factory;

  public BindingServerTransportFilter(Supplier<BindableService> factory) {
    this.factory = factory;
  }

  @Override
  public Attributes transportReady(Attributes transportAttrs) {
    return Attributes.newBuilder(transportAttrs).set(PEER_CONTEXT, new PeerContext(factory)).build();
  }

  @Override
  public void transportTerminated(Attributes transportAttrs) {
    // nothing.
  }

}
