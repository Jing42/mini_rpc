package com.jing.rpc.transport;

import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.serializer.CommonSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest request);

    void setSerializer(CommonSerializer serializer);
}
