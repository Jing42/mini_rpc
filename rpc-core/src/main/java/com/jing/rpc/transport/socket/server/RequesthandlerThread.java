package com.jing.rpc.transport.socket.server;

import com.esotericsoftware.kryo.io.Output;
import com.jing.rpc.handler.RequestHandler;
import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.transport.socket.util.ObjectReader;
import com.jing.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            System.out.println("here we are");
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            System.out.println("here this");
            Object result = requestHandler.handle(rpcRequest);
            System.out.println("handle success");
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
            System.out.println("write sucess");
        } catch (IOException e) {
            logger.error("error happens during call or send: ", e);
        }

    }
}
