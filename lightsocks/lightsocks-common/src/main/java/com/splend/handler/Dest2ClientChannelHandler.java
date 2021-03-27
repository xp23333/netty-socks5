package com.splend.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dest2ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private Channel clientChannelFuture;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Dest2ClientChannelHandler(Channel Client2DestChannelHandler) {
        this.clientChannelFuture = Client2DestChannelHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(Unpooled.copiedBuffer(Unpooled.EMPTY_BUFFER));
        logger.debug("active ...");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("inactive ...");
        if (clientChannelFuture.isActive()) {
            clientChannelFuture.writeAndFlush(Unpooled.copiedBuffer(Unpooled.EMPTY_BUFFER)).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        clientChannelFuture.closeFuture().addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (clientChannelFuture.isActive()) {
            logger.debug("writeAndFlush to ClientChannelFuture");
            clientChannelFuture.writeAndFlush(msg);
        } else {
            logger.debug("clientChannelFuture is inactive ...");
        }
    }
}
