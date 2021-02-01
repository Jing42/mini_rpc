package com.jing.rpc.transport.netty.client;

import com.jing.rpc.enumeration.RpcError;
import com.jing.rpc.exception.RpcException;
import com.jing.rpc.registry.NacosServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.transport.RpcClient;
import com.jing.rpc.codec.CommonDecoder;
import com.jing.rpc.codec.CommonEncoder;
import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;

    public NettyClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RpcRequest request) {
        if(serializer == null) {
            logger.error("did not set serializer!");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if(channel.isActive()) {
                channel.writeAndFlush(request).addListener(future1 -> {
                    if(future1.isSuccess()) {
                        logger.info(String.format("client send message: %s", request.toString()));
                    } else {
                        logger.error("error happens while sending message: ", future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + request.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
            } else {
                System.exit(0);
            }
        } catch(InterruptedException e) {
            logger.error("error happens while sending message！", e);
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
