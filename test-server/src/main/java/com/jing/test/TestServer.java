package com.jing.test;

import com.jing.rpc.api.HelloService;
import com.jing.rpc.registry.DefaultServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.socket.server.SocketServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.start(9000);
    }
}
