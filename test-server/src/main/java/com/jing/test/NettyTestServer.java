package com.jing.test;

import com.jing.rpc.api.HelloService;
import com.jing.rpc.transport.netty.server.NettyServer;
import com.jing.rpc.provider.ServiceProviderImpl;

public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1", 9999);
        server.publishService(helloService, "helloService");


    }
}
