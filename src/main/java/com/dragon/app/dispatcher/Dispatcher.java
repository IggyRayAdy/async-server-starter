package com.dragon.app.dispatcher;

import com.dragon.app.dto.HttpMethod;
import com.dragon.app.dto.HttpRequest;
import com.dragon.app.dto.HttpResponse;
import com.dragon.app.error.CustomError;
import com.dragon.app.router.annotation.RestRouter;
import com.dragon.app.router.annotation.RouterMapping;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Dispatcher {

    private static final String DEFAULT_ROUTERS_PATH = "com.dragon.app.router";

    private final Map<String, BiConsumer<HttpRequest, HttpResponse>> resources;

    private static volatile Dispatcher dispatcher;

    private Dispatcher() {
        resources = initResources();
    }

    public static Dispatcher getDispatcher() {
        Dispatcher instance = dispatcher;
        if (instance == null) {
            synchronized (Dispatcher.class) {
                instance = dispatcher;
                if (instance == null) {
                    dispatcher = instance = new Dispatcher();
                }
            }
        }
        return instance;
    }

    private static Map<String, BiConsumer<HttpRequest, HttpResponse>> initResources() {

        var requestResolver = new HashMap<String, BiConsumer<HttpRequest, HttpResponse>>();

        Set<Class<?>> classes = new Reflections(DEFAULT_ROUTERS_PATH)
                .getTypesAnnotatedWith(RestRouter.class);

        for (Class<?> clazz : classes) {
            String routerPath = clazz.getAnnotation(RestRouter.class).routerPath();

            Object instance;
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(RouterMapping.class)) {
                    continue;
                }

                RouterMapping annotation = method.getAnnotation(RouterMapping.class);
                HttpMethod http = annotation.method();
                String path = annotation.path();

                BiConsumer<HttpRequest, HttpResponse> function = (req, res) -> {
                    try {
                        method.invoke(instance, req, res);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                };

                String key = String.format("%s%s %s", routerPath, path, http);

                requestResolver.merge(key, function, (k1, k2) -> {
                    throw new RuntimeException("Path duplicates" + key);
                });
            }
        }
        return Collections.unmodifiableMap(requestResolver);
    }

    public void handle(HttpRequest request, HttpResponse response) {
        this.resolve(request).accept(request, response);
    }

    public BiConsumer<HttpRequest, HttpResponse> resolve(HttpRequest request) {
        return resources.getOrDefault(request.getPathMethod(), (req, res) -> {
            throw new CustomError("Page not found: " + request.getPathMethod(), "404");
        });
    }
}