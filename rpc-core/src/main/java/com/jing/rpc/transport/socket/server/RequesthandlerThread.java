package com.jing.rpc.transport.socket.server;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.jing.rpc.handler.RequestHandler;
import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequesthandlerThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public RequesthandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegistry,
                                CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(objectOutputStream, response, serializer);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("error happens during call or send: ", e);
        }

    }
}
