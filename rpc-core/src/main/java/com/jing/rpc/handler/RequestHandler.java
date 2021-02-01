package com.jing.rpc.handler;

import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.enumeration.ResponseCode;
import com.jing.rpc.provider.ServiceProvider;
import com.jing.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest rpcRequest) {
        Object result = null;
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("service:{} successfully invoke method {}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch(IllegalAccessException | InvocationTargetException e) {
            logger.error("error happens while invoke or transfer: ", e);
        }
        return result;
    }

    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service)
        throws IllegalAccessException, InvocationTargetException {

        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());

    }



}
