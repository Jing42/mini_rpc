package com.jing.rpc.enumeration;

public enum ResponseCode {

    SUCCESS(200, "call success"),
    FAIL(500, "call fail"),
    NOT_FOUND_METHOD(500, "can't find method"),
    NOT_FOUND_CLASS(500, "can't find class");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
