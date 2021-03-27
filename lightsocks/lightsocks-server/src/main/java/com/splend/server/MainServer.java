package com.splend.server;

import com.splend.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainServer {
    private static Logger logger = LoggerFactory.getLogger(MainServer.class);

    public static void main(String[] args) {
        InputStream inputStream = MainServer.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("config error");
            return;
        }
        try {
//            String LOCAL_HOST = properties.getProperty("LOCAL_HOST");
//            Integer LOCAL_PORT = Integer.valueOf(properties.getProperty("LOCAL_PORT"));
//            Config.LOCAL_HOST = LOCAL_HOST;
//            Config.LOCAL_PORT = LOCAL_PORT;
            String SERVER_HOST = properties.getProperty("SERVER_HOST");
            Integer SERVER_PORT = Integer.valueOf(properties.getProperty("SERVER_PORT"));
            Config.SERVER_HOST = SERVER_HOST;
            Config.SERVER_PORT = SERVER_PORT;
        } catch (Exception e) {
            System.out.println(e);
            logger.debug("config error");
        }

        Socks5Server socks5Server = new Socks5Server();
        socks5Server.start();
    }
}
