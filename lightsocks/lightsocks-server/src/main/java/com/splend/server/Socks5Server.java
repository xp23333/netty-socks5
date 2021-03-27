package com.splend.server;

import com.splend.config.Config;
import com.splend.server.handler.SocketServerHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Socks5Server {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void start() {


        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(4);
        logger.debug("server starting ...");
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                .handler(new LoggingHandler(LogLevel.ERROR))
                .childHandler(new SocketServerHandlerInitializer());
        try {
            logger.debug("server started port : " + Config.SERVER_PORT);
            bootstrap.bind(Config.SERVER_HOST, Config.SERVER_PORT).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            shutdown();
        }
    }

    public void shutdown() {
        logger.debug("server shutdown ...");
        if (bossGroup != null)
            bossGroup.shutdownGracefully();
        if (workerGroup != null)
            workerGroup.shutdownGracefully();
    }


}
