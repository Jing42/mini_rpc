package com.jing.rpc.enumeration;

public enum SerializerCode {

    JSON(1);

    public int getCode() {
        return code;
    }

    SerializerCode(int code) {
        this.code = code;
    }

    private final int code;
}
