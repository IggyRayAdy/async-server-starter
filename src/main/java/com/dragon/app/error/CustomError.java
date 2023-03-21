package com.dragon.app.error;

public class CustomError extends RuntimeException {

    private final String message;
    private final String code;

    public CustomError(String message, String code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
