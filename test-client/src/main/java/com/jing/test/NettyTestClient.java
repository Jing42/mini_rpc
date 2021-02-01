package com.jing.test;

import com.esotericsoftware.kryo.Kryo;
import com.jing.rpc.serializer.KryoSerializer;
import com.jing.rpc.transport.RpcClient;
import com.jing.rpc.api.HelloObject;
import com.jing.rpc.api.HelloService;
import com.jing.rpc.transport.RpcClientProxy;
import com.jing.rpc.transport.netty.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new KryoSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(24, "this is a message!!!");

        String res = helloService.hello(object);
        System.out.println(res);
    }
}
