package com.dragon.app.router;


import com.dragon.app.dto.HttpMethod;
import com.dragon.app.dto.HttpRequest;
import com.dragon.app.dto.HttpResponse;
import com.dragon.app.router.annotation.RestRouter;
import com.dragon.app.router.annotation.RouterMapping;

@SuppressWarnings("unused")
@RestRouter(routerPath = "/greeting")
public class GreetingRouter implements Router {

    @RouterMapping(method = HttpMethod.GET)
    public void doGet(HttpRequest request, HttpResponse response) {
        var name = request.getQueryParam("name");
        response.setBody(("Hello " + name).getBytes());
    }

    @RouterMapping(path = "/post", method = HttpMethod.POST)
    public void doPost(HttpRequest request, HttpResponse response) {
        response.setBody("Hello guest".getBytes());
    }
}
