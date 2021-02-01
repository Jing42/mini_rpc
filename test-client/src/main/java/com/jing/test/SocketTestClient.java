package com.jing.test;

import com.jing.rpc.api.HelloObject;
import com.jing.rpc.api.HelloService;
import com.jing.rpc.serializer.KryoSerializer;
import com.jing.rpc.transport.RpcClientProxy;
import com.jing.rpc.transport.socket.client.SocketClient;

public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        client.setSerializer(new KryoSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
