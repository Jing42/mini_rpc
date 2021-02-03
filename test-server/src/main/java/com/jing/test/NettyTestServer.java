package com.jing.test;

import com.jing.rpc.annotation.ServiceScan;
import com.jing.rpc.api.HelloService;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.transport.RpcServer;
import com.jing.rpc.transport.netty.server.NettyServer;
import com.jing.rpc.provider.ServiceProviderImpl;

@ServiceScan
public class NettyTestServer {

    public static void main(String[] args) {

        RpcServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.KRYO_SERIALIZER);
        server.start();
    }
}
