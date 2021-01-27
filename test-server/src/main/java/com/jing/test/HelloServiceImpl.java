package com.jing.test;

import com.jing.rpc.api.HelloObject;
import com.jing.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    public String hello(HelloObject object) {
        logger.info("receive: {}", object.getMessage());
        return object.getId() + ": " + object.getMessage();
    }
}
