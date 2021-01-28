package com.jing.rpc.enumeration;

public enum PackageType {

    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    public int getCode() {
        return code;
    }

    PackageType(int code) {
        this.code = code;
    }

    private final int code;
}
