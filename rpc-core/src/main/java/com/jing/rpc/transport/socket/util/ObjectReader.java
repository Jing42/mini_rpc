package com.jing.rpc.transport.socket.util;

import com.jing.rpc.entity.RpcRequest;
import com.jing.rpc.entity.RpcResponse;
import com.jing.rpc.enumeration.PackageType;
import com.jing.rpc.enumeration.RpcError;
import com.jing.rpc.exception.RpcException;
import com.jing.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ObjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);
    private  static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream in) throws IOException {
        byte[] numberBytes = new byte[4];

        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        if(magic != MAGIC_NUMBER) {
            logger.error("unknown protocol package!");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if(packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("unknown data package: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            logger.error("unknown serializer: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        in.read(numberBytes);
        int length = bytesToInt(numberBytes);

        byte[] bytes = new byte[length];
        in.read(bytes);
        return serializer.deserialize(bytes, packageClass);
    }

    public static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF);
        return value;
    }
}