package com.jing.rpc.enumeration;

public enum RpcError {

    SERVICE_INVOCATION_FAILURE("service call fail!"),
    SERVICE_NOT_FOUND("can't find service!"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("service not implement interface!"),
    UNKNOWN_PROTOCOL("unknown protocol!"),
    UNKNOWN_PACKAGE_TYPE("unknown package type!"),
    UNKNOWN_SERIALIZER("unknow serializer"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("failed to connect to service registry!"),
    REGISTER_SERVICE_FAILED("failed to register service!");

    private final String message;

    RpcError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
