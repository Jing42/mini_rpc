package com.jing.rpc.transport;

import com.jing.rpc.provider.ServiceProvider;
import com.jing.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public abstract class AbstractRpcServer implements RpcServer{

    protected Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;


}
