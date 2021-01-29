package com.jing.rpc.enumeration;

public enum SerializerCode {

    KRYO(0),
    JSON(1);

    public int getCode() {
        return code;
    }

    SerializerCode(int code) {
        this.code = code;
    }

    private final int code;
}
