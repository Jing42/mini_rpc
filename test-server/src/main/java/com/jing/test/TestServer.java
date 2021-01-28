package com.jing.test;

import com.jing.rpc.api.HelloService;
import com.jing.rpc.registry.DefaultServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
