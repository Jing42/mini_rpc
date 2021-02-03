package com.jing.rpc.transport;

import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.transport.netty.client.NettyClient;
import com.jing.rpc.transport.socket.client.SocketClient;
import com.jing.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        RpcResponse rpcResponse = null;
        if(client instanceof NettyClient) {
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
            try {
                rpcResponse = completableFuture.get();
            } catch(InterruptedException | ExecutionException e) {
                logger.error("send method call fail!", e);
                return null;
            }
        }
        if(client instanceof SocketClient) {
            rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
        }

        RpcMessageChecker.check(rpcRequest, rpcResponse);
        return rpcResponse.getData();
    }
}
