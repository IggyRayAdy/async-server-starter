package com.dragon.app;

import com.dragon.app.server.AsyncServer;

public class App {

    public static void main(String[] args) {
        Integer port = null;
        String host = null;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            host = args[1];
        }

        new AsyncServer(port, host).bootstrap();
    }
}