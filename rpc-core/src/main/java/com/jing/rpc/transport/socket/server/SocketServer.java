package com.jing.rpc.transport.socket.server;

import com.jing.rpc.enumeration.RpcError;
import com.jing.rpc.exception.RpcException;
import com.jing.rpc.provider.ServiceProvider;
import com.jing.rpc.provider.ServiceProviderImpl;
import com.jing.rpc.registry.NacosServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.serializer.CommonSerializer;
import com.jing.rpc.transport.AbstractRpcServer;
import com.jing.rpc.transport.RpcServer;
import com.jing.rpc.handler.RequestHandler;
import com.jing.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer extends AbstractRpcServer {


    private RequestHandler requestHandler = new RequestHandler();

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private final ExecutorService threadPool;
    private CommonSerializer serializer;

    private final String host;
    private final int port;



    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);


    public SocketServer(String host, int port) {
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();

        this.host = host;
        this.port = port;

        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("server is starting...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("client ip is: {} port: {}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequesthandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("error occur while connection: ", e);
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("serializer unset!");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
