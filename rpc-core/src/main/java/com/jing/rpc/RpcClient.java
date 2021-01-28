package com.jing.rpc;

import com.jing.rpc.entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest request);
}
