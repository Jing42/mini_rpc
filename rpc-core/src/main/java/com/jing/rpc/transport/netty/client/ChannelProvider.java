package com.jing.rpc.transport.netty.client;

import com.jing.rpc.codec.CommonDecoder;
import com.jing.rpc.codec.CommonEncoder;
import com.jing.rpc.enumeration.RpcError;
import com.jing.rpc.exception.RpcException;
import com.jing.rpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                channel.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);

        try {
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("error happens while acquire channel:", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {

            if (future.isSuccess()) {
                logger.info("client connect success!");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }

            if(retry == 0) {
                logger.error("client connect fail: retry count used up, give up connection!");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }

            int order = (MAX_RETRY_COUNT - retry) + 1;
            int delay = 1 << order;
            logger.error("{}: connect fail, the {} time to reconnect......", new Date(), order);
            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry-1, countDownLatch),
                    delay, TimeUnit.SECONDS);
        });
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
