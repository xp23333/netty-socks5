package com.splend.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client2DestChannelHandler extends ChannelInboundHandlerAdapter {

    private Channel destChannelFuture;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Client2DestChannelHandler(Channel destChannelFuture) {
        this.destChannelFuture = destChannelFuture;
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
        if (destChannelFuture.isActive()) {
            destChannelFuture.writeAndFlush(Unpooled.copiedBuffer(Unpooled.EMPTY_BUFFER)).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        destChannelFuture.closeFuture().addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (destChannelFuture.isActive()) {
            logger.debug("writeAndFlush to destChannelFuture");
            destChannelFuture.writeAndFlush(msg);
        } else {
            logger.debug("destChannelFuture is inactive ...");
        }
    }
}
