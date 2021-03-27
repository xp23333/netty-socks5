package com.splend.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class SocketServerHandler extends SimpleChannelInboundHandler<SocksRequest> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SocksRequest socksRequest) throws Exception {
        logger.debug("msg : " + socksRequest.toString());
        switch (socksRequest.requestType()) {
            case INIT:
                logger.debug("INIT REQUEST ...");
                ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
                ctx.writeAndFlush(new SocksInitResponse(SocksAuthScheme.NO_AUTH));
                break;
            case AUTH:
                logger.debug("AUTH REQUEST ...");
                ctx.pipeline().addFirst(new SocksCmdRequestDecoder());
                ctx.writeAndFlush(new SocksAuthResponse(SocksAuthStatus.SUCCESS));
                break;
            case CMD:
                logger.debug("CMD REQUEST ...");
                SocksCmdRequest socksCmdRequest = (SocksCmdRequest) socksRequest;
                if (socksCmdRequest.cmdType() == SocksCmdType.CONNECT) {
                    // 如果已经建立起连接，直接交给SocksServerConnectHandler
                    ctx.pipeline().addLast(new SocksServerConnectHandler());
                    // 删除当前的 handler
                    ctx.pipeline().remove(this);
                    // 传递到下一个处理器也就是SocksServerConnectHandler，没有这一句，就不会传递到后续handler
                    ctx.fireChannelRead(socksCmdRequest);
                } else {
                    ctx.close();
                }
                break;
            case UNKNOWN:
                logger.debug("UNKNOWN REQUEST ...");
                ctx.close();
        }
    }
}
