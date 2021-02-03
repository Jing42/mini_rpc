package com.jing.rpc.transport.netty.server;

import com.jing.rpc.handler.RequestHandler;
import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.factory.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static final String Thread_NAME_PREFIX = "netty-server-handler";
    private static final ExecutorService threadPool;

    static {
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.createDefaultThreadPool(Thread_NAME_PREFIX);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            logger.info("server receive request: {}", rpcRequest);

            Object result = requestHandler.handle(rpcRequest);

            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result, rpcRequest.getRequestId()));
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(rpcRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("error happens while handle method invoke");
        cause.printStackTrace();;
        ctx.close();
    }
}
