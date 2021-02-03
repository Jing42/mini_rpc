package com.jing.rpc.transport.netty.client;

import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.factory.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        try {
            logger.info(String.format("client receive message: %s", rpcResponse));
            unprocessedRequests.complete(rpcResponse);
        } finally {
            ReferenceCountUtil.release(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("error happens while method invocation!");
        cause.printStackTrace();
        ctx.close();
    }
}
