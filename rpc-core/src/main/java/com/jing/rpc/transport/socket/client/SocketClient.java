package com.jing.rpc.transport.socket.client;

import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.enumeration.ResponseCode;
import com.jing.rpc.enumeration.RpcError;
import com.jing.rpc.exception.RpcException;
import com.jing.rpc.registry.NacosServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.transport.RpcClient;
import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.transport.socket.util.ObjectReader;
import com.jing.rpc.transport.socket.util.ObjectWriter;
import com.jing.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;

    public SocketClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("serializer unset!");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());

        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);

            RpcResponse rpcResponse = (RpcResponse) obj;
            if(rpcResponse == null) {
                logger.error("call service {} fail", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if(rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("call service {} fail, response {}", rpcRequest.getInterfaceName(), rpcResponse);
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("there is error happen during call ", e);
            throw new RpcException("service call fail!: ", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
