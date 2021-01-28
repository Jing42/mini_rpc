package com.jing.test;

import com.jing.rpc.RpcClient;
import com.jing.rpc.api.HelloObject;
import com.jing.rpc.api.HelloService;
import com.jing.rpc.client.RpcClientProxy;
import com.jing.rpc.netty.client.NettyClient;

public class NettyTestClient {

    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);

        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(24, "this is a message");

        String res = helloService.hello(object);
        System.out.println(res);
    }
}
