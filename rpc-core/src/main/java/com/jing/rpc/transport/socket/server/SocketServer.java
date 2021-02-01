package com.jing.rpc.transport.socket.server;

import com.jing.rpc.registry.NacosServiceRegistry;
import com.jing.rpc.registry.ServiceRegistry;
import com.jing.rpc.transport.RpcServer;
import com.jing.rpc.provider.ServiceRegistry;
import com.jing.rpc.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RpcServer {


    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private final ExecutorService threadPool;


    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);


    public SocketServer(String host, int port) {
        serviceRegistry = new NacosServiceRegistry();
        this.host = host;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("server is starting...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                logger.info("client ip is: {} port: ", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequesthandlerThread(socket, requestHandler, serviceRegistry));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("error occur while connection: ", e);
        }
    }

}
