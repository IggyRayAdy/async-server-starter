package com.dragon.app.router;


import com.dragon.app.dto.HttpRequest;
import com.dragon.app.dto.HttpResponse;
import com.dragon.app.router.annotation.RestRouter;
import com.dragon.app.router.annotation.RouterMapping;

import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("unused")
@RestRouter(routerPath = "/good")
public class GoodRouter implements Router {

    @RouterMapping
    public void doGet(HttpRequest request, HttpResponse response) {

        var filePath = Path.of("files/example.png");

        try {
            response.setBody(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}