package com.dragon.app.dto;


import com.dragon.app.error.CustomError;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record HttpRequest(String message,
                          HttpMethod httpMethod,
                          String path,
                          Map<String, String> headers,
                          Map<String, String> queryParams,
                          String body
) {
    private static final String DELIMITER = "\r\n\r\n";
    private static final String NEW_LINE = "\r\n";
    private static final String HEADER_DELIMITER = ":";

    public static HttpRequest of(String message) {
        try {
            String[] parts = message.split(DELIMITER);

            String[] headers = parts[0].split(NEW_LINE);

            String[] firstLine = headers[0].split(" ", 3);

            var httpMethod = HttpMethod.valueOf(firstLine[0]);
            URI uri = new URI(firstLine[1]);

            var path = uri.getPath();

            Map<String, String> queryParams = fillInQueryParams(uri.getQuery());

            Map<String, String> headers1 = fillInHeaders(headers);

            int contentLength = Integer.parseInt(headers1.getOrDefault("Content-Length", "0"));

            var body = contentLength == 0
                    ? ""
                    : parts[1];

            return new HttpRequest(message, httpMethod, path, headers1, queryParams, body);
        } catch (Exception e) {
            throw new CustomError("Bad Request", "400");
        }
    }

    private static Map<String, String> fillInHeaders(String[] lines) {
        Map<String, String> result = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String[] parts = lines[i].split(HEADER_DELIMITER, 2);
            result.put(parts[0].trim(), parts[1].trim());
        }

        return Collections.unmodifiableMap(result);
    }

    private static Map<String, String> fillInQueryParams(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();

        Map<String, String> result = new HashMap<>();
        for (String part : query.split("&")) {
            String[] parts = part.split("=", 2);
            result.put(parts[0], parts[1]);
        }

        return Collections.unmodifiableMap(result);
    }

    public String getQueryParam(String paramKey) throws CustomError {
        return queryParams.getOrDefault(paramKey, "12132");
    }

    public String getPathMethod() {
        return String.format("%s %s", path, httpMethod);
    }
}

