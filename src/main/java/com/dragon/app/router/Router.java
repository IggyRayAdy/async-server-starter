package com.dragon.app.router;


import com.dragon.app.dto.HttpRequest;
import com.dragon.app.dto.HttpResponse;

@SuppressWarnings("unused")
public interface Router {
    default void doGet(HttpRequest request, HttpResponse response) {
        throw new RuntimeException("Not Found " + request.getPathMethod());
    }

    default void doPost(HttpRequest request, HttpResponse response) {
        throw new RuntimeException("Not Found " + request.getPathMethod());
    }
}
