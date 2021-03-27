package com.splend.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectClientHandler extends SimpleChannelInboundHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Promise<Channel> promise;

    public DirectClientHandler(Promise<Channel> promise) {
        this.promise = promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        logger.debug("msg : " + o.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("active ...");
        ctx.channel().pipeline().remove(this);
        promise.setSuccess(ctx.channel());
    }
}
