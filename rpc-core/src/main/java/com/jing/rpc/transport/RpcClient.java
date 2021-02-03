package com.jing.rpc.transport;

import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.serializer.CommonSerializer;

public interface RpcClient {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest request);

}
