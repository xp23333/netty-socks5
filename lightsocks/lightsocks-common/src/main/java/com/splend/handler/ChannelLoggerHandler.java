package com.splend.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelLoggerHandler extends SimpleChannelInboundHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        logger.debug("filter msg : " + msg.toString());
        channelHandlerContext.fireChannelRead(msg);
    }
}
