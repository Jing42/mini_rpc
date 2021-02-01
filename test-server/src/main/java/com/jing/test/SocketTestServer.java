package com.jing.test;

import com.jing.rpc.api.HelloService;
import com.jing.rpc.provider.ServiceProvider;
import com.jing.rpc.provider.ServiceProviderImpl;
import com.jing.rpc.serializer.KryoSerializer;
import com.jing.rpc.transport.socket.server.SocketServer;

public class SocketTestServer {
    public static void main(String[] args) {

        HelloService helloService = new HelloServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        socketServer.setSerializer(new KryoSerializer());
        socketServer.publishService(helloService, HelloService.class);

    }
}
