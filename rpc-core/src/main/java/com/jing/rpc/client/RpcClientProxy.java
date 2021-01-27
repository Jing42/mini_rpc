package com.jing.rpc.client;

import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

public class RpcClientProxy implements InvocationHandler {

    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameters(objects);
        rpcRequest.setParamTypes(method.getParameterTypes());

        RpcClient rpcClient = new RpcClient();
        return ((RpcResponse)rpcClient.sendRequest(rpcRequest, host, port)).getData();
    }
}
