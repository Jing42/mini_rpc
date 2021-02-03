package com.jing.test;

import com.jing.rpc.annotation.Service;
import com.jing.rpc.api.ByeService;

@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
