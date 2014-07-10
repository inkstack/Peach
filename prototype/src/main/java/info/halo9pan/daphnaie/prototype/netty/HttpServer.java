package info.halo9pan.daphnaie.prototype.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {
	public void start(int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
			        .channel(NioServerSocketChannel.class) // (3)
			        .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
				        @Override
				        public void initChannel(SocketChannel ch)
				                throws Exception {
					        // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
					        ch.pipeline().addLast(
					                new HttpResponseEncoder());
					        // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
					        ch.pipeline().addLast(
					                new HttpRequestDecoder());
					        ch.pipeline().addLast(
					                new HttpServerInboundHandler());
				        }
			        }).option(ChannelOption.SO_BACKLOG, 128) // (5)
			        .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			ChannelFuture f = b.bind(port).sync(); // (7)

			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		HttpServer server = new HttpServer();
		server.start(8000);
	}
}
