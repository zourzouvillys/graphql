package io.zrz.zulu.server.netty.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.util.AttributeKey;
import io.reactivex.Flowable;

public class GraphQLFrameHandler extends SimpleChannelInboundHandler<GQLWSFrame> {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GraphQLFrameHandler.class);
  private final SessionRepository sessions;

  private final static AttributeKey<GQLWebSocketSession> SESSION = AttributeKey.valueOf("graphql-ws-session");

  public GraphQLFrameHandler(final SessionRepository sessions) {
    this.sessions = sessions;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final GQLWSFrame msg) throws Exception {

    final GQLWebSocketSession session = ctx.channel().attr(SESSION).get();

    if (session != null) {
      log.trace("received in-session frame: {}", msg);
      session.process(msg);
      return;
    }

    switch (msg.type().kindName()) {
      case "connection_init":
        this.processInit(ctx, msg);
        break;
      default:
        log.warn("unknown graphql-ws frame outside of session: {}", msg);
        return;
    }

  }

  private void processInit(final ChannelHandlerContext ctx, final GQLWSFrame msg) {
    log.info("initialized new session");
    final GQLWebSocketSession session = this.sessions.initializeSession(msg.rawPayload(), Flowable.empty());
    ctx.channel().attr(SESSION).set(session);
    ctx.writeAndFlush(SimpleGQLWSFrame.create(StandardGQLWSFrameKind.GQL_CONNECTION_ACK));
    session.subscribe(frame -> {
      ctx.writeAndFlush(frame);
    });
  }

  @Override
  public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
    if (evt instanceof HandshakeComplete) {
      // todo: set timeout?
    }
  }

}
