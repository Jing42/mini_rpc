package com.jing.test;

import com.jing.rpc.annotation.ServiceScan;
import com.jing.rpc.api.HelloService;
import com.jing.rpc.provider.ServiceProvider;
import com.jing.rpc.provider.ServiceProviderImpl;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.serializer.KryoSerializer;
import com.jing.rpc.transport.RpcServer;
import com.jing.rpc.transport.socket.server.SocketServer;

@ServiceScan
public class SocketTestServer {
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998, CommonSerializer.KRYO_SERIALIZER);
        server.start();
    }
}
