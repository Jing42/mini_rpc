package com.jing.rpc.transport;

import com.jing.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        logger.info("invoke method: {}#{}", method.getDeclaringClass().getName(), method.getName());

        RpcRequest rpcRequest = new RpcRequest(
                UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                objects,
                method.getParameterTypes()
        );

        Object o1 = client.sendRequest(rpcRequest);
        return o1;
    }
}
