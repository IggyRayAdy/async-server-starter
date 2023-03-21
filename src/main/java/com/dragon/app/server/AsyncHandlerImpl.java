package com.dragon.app.server;

import com.dragon.app.dispatcher.Dispatcher;
import com.dragon.app.dto.HttpRequest;
import com.dragon.app.dto.HttpResponse;
import com.dragon.app.error.CustomError;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class AsyncHandlerImpl extends Thread {

    private final Dispatcher dispatcher = Dispatcher.getDispatcher();

    private static final int BUFFER_SIZE = 512;

    private final AsynchronousSocketChannel client;

    public AsyncHandlerImpl(AsynchronousSocketChannel clientChannel) {
        this.client = clientChannel;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (InterruptedException | IOException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void handleClient()
            throws InterruptedException, ExecutionException, TimeoutException, IOException {

        String stringBuilder = getRequestString();

        var request = HttpRequest.of(stringBuilder);

        var response = new HttpResponse();

        handle(request, response);

        writeResponse(response);

        closeClientChannel(client);
    }

    private String getRequestString()
            throws InterruptedException, ExecutionException {

        var buffer = ByteBuffer.allocate(BUFFER_SIZE);

        var stringBuilder = new StringBuilder();

        boolean keepReading = true;

        while (keepReading) {
            var readIndex = client.read(buffer).get();

            keepReading = readIndex > BUFFER_SIZE;

            buffer.flip();

            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
            stringBuilder.append(charBuffer);

            buffer.clear();
        }

        return stringBuilder.toString();
    }

    public void handle(HttpRequest request, HttpResponse response) {
        try {
            dispatcher.handle(request, response);
        } catch (CustomError e) {
            e.printStackTrace();
            response.ofError(e);
        }
    }

    private void writeResponse(HttpResponse response) {
        try {
            ByteBuffer headers = ByteBuffer.wrap(response.getHeaders());
            ByteBuffer body = ByteBuffer.wrap(response.getBody());
            client.write(headers).get();
            client.write(body).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void closeClientChannel(AsynchronousSocketChannel clientChannel) {
        try {
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomError("Internal Error", "500");
        }
    }
}

