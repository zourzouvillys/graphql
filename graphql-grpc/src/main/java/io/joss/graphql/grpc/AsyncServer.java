package io.joss.graphql.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;

public class AsyncServer {

	public static void main(String... args) throws Exception {
		new AsyncServer().run(args);
	}

	/** Equivalent of "main", but non-static. */
	public void run(String[] args) throws Exception {

		final Server server = newServer();

		server.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("Server shutting down");
					server.shutdown();
					server.awaitTermination(5, TimeUnit.SECONDS);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	static Server newServer() throws IOException {
		SslContext sslContext = null;

		// if (config.tls) {
		// System.out.println("Using fake CA for TLS certificate.\n" + "Run the
		// Java client with --tls --testca");
		//
		// File cert = TestUtils.loadCert("server1.pem");
		// File key = TestUtils.loadCert("server1.key");
		// SslContextBuilder sslContextBuilder = GrpcSslContexts.forServer(cert,
		// key);
		// if (config.transport == ServerConfiguration.Transport.NETTY_NIO) {
		// sslContextBuilder = GrpcSslContexts.configure(sslContextBuilder,
		// SslProvider.JDK);
		// } else {
		// // Native transport with OpenSSL
		// sslContextBuilder = GrpcSslContexts.configure(sslContextBuilder,
		// SslProvider.OPENSSL);
		// }
		// if (config.useDefaultCiphers) {
		// sslContextBuilder.ciphers(null);
		// }
		// sslContext = sslContextBuilder.build();
		// }

		final EventLoopGroup boss;
		final EventLoopGroup worker;
		final Class<? extends ServerChannel> channelType;
		// switch (config.transport) {
		// case NETTY_NIO: {
		boss = new NioEventLoopGroup();
		worker = new NioEventLoopGroup();
		channelType = NioServerSocketChannel.class;
		// break;
		// }
		// case NETTY_EPOLL: {
		// try {
		// // These classes are only available on linux.
		// Class<?> groupClass =
		// Class.forName("io.netty.channel.epoll.EpollEventLoopGroup");
		// @SuppressWarnings("unchecked")
		// Class<? extends ServerChannel> channelClass = (Class<? extends
		// ServerChannel>) Class
		// .forName("io.netty.channel.epoll.EpollServerSocketChannel");
		// boss = (EventLoopGroup) groupClass.newInstance();
		// worker = (EventLoopGroup) groupClass.newInstance();
		// channelType = channelClass;
		// break;
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// case NETTY_UNIX_DOMAIN_SOCKET: {
		// try {
		// // These classes are only available on linux.
		// Class<?> groupClass =
		// Class.forName("io.netty.channel.epoll.EpollEventLoopGroup");
		// @SuppressWarnings("unchecked")
		// Class<? extends ServerChannel> channelClass = (Class<? extends
		// ServerChannel>) Class
		// .forName("io.netty.channel.epoll.EpollServerDomainSocketChannel");
		// boss = (EventLoopGroup) groupClass.newInstance();
		// worker = (EventLoopGroup) groupClass.newInstance();
		// channelType = channelClass;
		// break;
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		// }
		// default: {
		// // Should never get here.
		// throw new IllegalArgumentException("Unsupported transport: " +
		// config.transport);
		// }
		// }

		NettyServerBuilder builder = NettyServerBuilder
				//
				.forPort(3333)
				// .forAddress(config.address)
				.bossEventLoopGroup(new NioEventLoopGroup())
				//
				.workerEventLoopGroup(new NioEventLoopGroup())
				//
				.channelType(NioServerSocketChannel.class)
				// .addService(new BenchmarkServiceImpl())
				.sslContext(sslContext)
				//
				.flowControlWindow(NettyChannelBuilder.DEFAULT_FLOW_CONTROL_WINDOW)

		;

		// if (config.directExecutor) {
		builder.directExecutor();
		// }

		return builder.build();
	}

}