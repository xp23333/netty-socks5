package com.splend.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;

public class SocketServerHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private SocksMessageEncoder socksMessageEncoder = new SocksMessageEncoder();
    private SocketServerHandler socketServerHandler = new SocketServerHandler();

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new SocksInitRequestDecoder())
                .addLast(socksMessageEncoder)
                .addLast(socketServerHandler);
    }
}
