package com.dragon.app.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AsyncServer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private volatile boolean isShutdown = false;

    private String host = "127.0.0.1";
    private Integer port = 8080;

    public AsyncServer(Integer port, String host) {
        this.port = port != null ? port : this.port;
        this.host = host != null ? host : this.host;
    }

    public AsyncServer() {
    }

    public void bootstrap() {

        handleKillSignal();

        try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
            logger.info("Starting async server ...");

            server.bind(new InetSocketAddress(host, port));
            while (!isShutdown) {
                Future<AsynchronousSocketChannel> future = server.accept();
                executorService.submit(new AsyncHandlerImpl(future.get()));
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    private void shutdown() {
        try {
            isShutdown = true;
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleKillSignal() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down async server ...");
            shutdown();
        }));
    }
}