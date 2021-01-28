package com.jing.test;

import com.jing.rpc.api.HelloService;
import com.jing.rpc.netty.server.NettyServer;
import com.jing.rpc.registry.DefaultServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);

        NettyServer server = new NettyServer();
        server.start(9999);

    }
}
