package com.jing.test;

import com.jing.rpc.api.HelloService;
import com.jing.rpc.provider.ServiceProviderImpl;
import com.jing.rpc.provider.ServiceRegistry;
import com.jing.rpc.transport.socket.server.SocketServer;

public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new ServiceProviderImpl();
        serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.start(9000);
    }
}
