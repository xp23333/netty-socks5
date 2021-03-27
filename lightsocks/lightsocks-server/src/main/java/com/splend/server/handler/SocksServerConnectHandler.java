package com.splend.server.handler;

import com.splend.handler.Client2DestChannelHandler;
import com.splend.handler.Dest2ClientChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksCmdResponse;
import io.netty.handler.codec.socks.SocksCmdStatus;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocksServerConnectHandler extends SimpleChannelInboundHandler<SocksCmdRequest> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SocksCmdRequest socksCmdRequest) {
        logger.debug("msg : " + socksCmdRequest.toString());
        Promise<Channel> promise = ctx.executor().newPromise();
        Channel clientChannel = ctx.channel();
        promise.addListener((GenericFutureListener<Future<Channel>>) future -> {
            final Channel destChannel = future.getNow();
            if (future.isSuccess()) {
                // 回应客户端 请求成功，可以进行数据包传输
                clientChannel.writeAndFlush(new SocksCmdResponse(SocksCmdStatus.SUCCESS, socksCmdRequest.addressType())).addListener(
                        (ChannelFutureListener) channelFuture -> {
                            logger.debug("Send SocksCmdResponse(Success) to Client and Establish Client2Dest, Dest2Client Channel");
                            ctx.pipeline().remove(SocksServerConnectHandler.this);
                            destChannel.pipeline().addLast(new Dest2ClientChannelHandler(clientChannel));
                            ctx.pipeline().addLast(new Client2DestChannelHandler(destChannel));
                        }
                );


            } else {
                logger.debug("Send SocksCmdResponse(Failture) to Client");
                clientChannel.writeAndFlush(new SocksCmdResponse(
                        SocksCmdStatus.FAILURE, socksCmdRequest.addressType()));
                clientChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }

        });


        Bootstrap b = new Bootstrap();
        b.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise)); // 被激活然后设置promise

        String dstAddr = socksCmdRequest.host();
        Integer dstPort = socksCmdRequest.port();

        b.connect(socksCmdRequest.host(), socksCmdRequest.port()).
                addListener((ChannelFutureListener) future -> {
                            {
                                if (future.isSuccess()) {
                                    logger.debug("Server connect host: " + dstAddr + " port : " + dstPort + " successfuly ");
                                } else {
                                    clientChannel.writeAndFlush(new SocksCmdResponse(
                                            SocksCmdStatus.FAILURE, socksCmdRequest.addressType()));
                                    if (clientChannel.isActive()) {
                                        clientChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                                    }
                                    logger.debug("Server connect host: " + dstAddr + " port : " + dstPort + " failed");
                                }
                            }
                        }
                );

    }

}
