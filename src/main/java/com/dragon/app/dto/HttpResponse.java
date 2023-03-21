package com.dragon.app.dto;

import com.dragon.app.error.CustomError;
import static com.dragon.app.utils.ContentType.TEXT_HTML_UTF8;
import static com.dragon.app.utils.ContentType.getContentTypes;
import com.dragon.app.utils.HttpHeader;
import static com.dragon.app.utils.HttpHeader.CONNECTION;
import static com.dragon.app.utils.HttpHeader.CONTENT_LENGTH;
import static com.dragon.app.utils.HttpHeader.SERVER;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String statusCode = "200";
    private String statusMessage = "OK";
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    private final static String NEW_LINE = "\r\n";

    public HttpResponse() {
        this.headers.put(SERVER, "dragon");
        this.headers.put(CONNECTION, "Close");
    }

    public String message() {
        StringBuilder builder = new StringBuilder();

        builder.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append(NEW_LINE);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(NEW_LINE);
        }

        return builder
                .append(NEW_LINE)
                .toString();
    }

    public byte[] getHeaders() {
        return message().getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
        this.headers.put(CONTENT_LENGTH, String.valueOf(body.length));
    }

    public void setBody(Path filePath) throws IOException {
        var format = getFileExtension(filePath);
        var contentType = getContentTypes(format);
        addHeader(HttpHeader.CONTENT_TYPE, contentType);
        setBody(Files.readAllBytes(filePath));
    }

    public void setBody(String body) {
        var bbody = body.getBytes(StandardCharsets.UTF_8);
        this.body = bbody;
        this.headers.put(CONTENT_LENGTH, String.valueOf(bbody.length));
    }

    public byte[] getBody() {
        return body;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void ofError(CustomError e) {
        this.setStatusCode(e.getCode());
        this.setStatusMessage(e.getMessage());
        this.addHeader(HttpHeader.CONTENT_TYPE, TEXT_HTML_UTF8);
        this.setBody("<html><body><h1>" + e.getMessage() + "</h1></body></html>");
    }

    private String getFileExtension(Path path) {
        var name = path.getFileName().toString();
        var extensionStart = name.lastIndexOf(".");
        return extensionStart == -1 ? "" : name.substring(extensionStart + 1);
    }
}
