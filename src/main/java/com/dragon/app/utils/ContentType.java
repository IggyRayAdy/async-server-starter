package com.dragon.app.utils;

public class ContentType {
    public static final String TEXT_HTML_UTF8 = "text/html; charset=utf-8";
    public static final String TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";
    public static final String IMAGE_JPEG = "image/png";
    public static final String APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";
    public static final String APPLICATION_XML_UTF8 = "application/xml; charset=utf-8";

    public static String getContentTypes(String format) {
        return switch (format) {
            case "jpg", "png" -> IMAGE_JPEG;
            case "json" -> APPLICATION_JSON_UTF8;
            case "txt" -> TEXT_PLAIN_UTF8;
            case null, "html", "", default -> TEXT_HTML_UTF8;
        };
    }
}