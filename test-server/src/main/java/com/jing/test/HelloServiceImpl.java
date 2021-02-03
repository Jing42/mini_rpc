package com.jing.test;

import com.jing.rpc.annotation.Service;
import com.jing.rpc.api.HelloObject;
import com.jing.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("receive: {}", object.getMessage());
        return object.getId() + ": " + object.getMessage();
    }
}
