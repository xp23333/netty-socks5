package com.splend.client.handler;

import com.splend.config.Config;
import com.splend.handler.Client2DestChannelHandler;
import com.splend.handler.Dest2ClientChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SocksClientConnectHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object o) throws Exception {
        Channel clientChannel = ctx.channel();
        Bootstrap b = new Bootstrap();
        b.group(ctx.channel()
                .eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        // 与远程服务器建立连接
                        socketChannel.pipeline().addLast(new Dest2ClientChannelHandler(clientChannel));
                    }
                });

        b.connect(Config.SERVER_HOST, Config.SERVER_PORT).
                addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        logger.debug("connect remote successfuly");
                        ctx.pipeline().remove(SocksClientConnectHandler.this);
                        ctx.pipeline().addLast(new Client2DestChannelHandler(channelFuture.channel()));
                        // 将消息传递到Client2DestChannelHandler，通过这个通道写入远程服务器
                        ctx.fireChannelRead(o);
                        // 或者下面这样直接写入
                        // channelFuture.channel().writeAndFlush(o);
                    } else {
                        clientChannel.close();
                        logger.debug("connect remote failed");
                    }
                });
    }
}
