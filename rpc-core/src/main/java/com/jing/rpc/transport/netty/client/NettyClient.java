package com.jing.rpc.transport.netty.client;

import com.jing.rpc.enumeration.RpcError;
import com.jing.rpc.exception.RpcException;
import com.jing.rpc.factory.SingletonFactory;
import com.jing.rpc.loadBalancer.LoadBalancer;
import com.jing.rpc.loadBalancer.RandomLoadBalancer;
import com.jing.rpc.registry.NacosServiceDiscovery;
import com.jing.rpc.registry.NacosServiceRegistry;
import com.jing.rpc.registry.ServiceDiscovery;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.transport.RpcClient;
import com.jing.rpc.codec.CommonDecoder;
import com.jing.rpc.codec.CommonEncoder;
import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.serializer.KryoSerializer;
import com.jing.rpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() { this(DEFAULT_SERIALIZER, new RandomLoadBalancer());}
    public NettyClient(LoadBalancer loadBalancer) { this(DEFAULT_SERIALIZER, loadBalancer); }
    public NettyClient(Integer serializer) { this(serializer, new RandomLoadBalancer());}
    public NettyClient(Integer serialier, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serialier);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request) {
        if(serializer == null) {
            logger.error("did not set serializer!");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();

        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);

            unprocessedRequests.put(request.getRequestId(), resultFuture);

            channel.writeAndFlush(request).addListener((ChannelFutureListener)future1 -> {
                if(future1.isSuccess()) {
                    logger.info(String.format("client send message: %s", request.toString()));
                } else {
                    future1.channel().close();
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("error happens while sending message: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequests.remove(request.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
