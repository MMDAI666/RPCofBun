package org.Bun.netty.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.Bun.entity.RpcRequest;
import org.Bun.enums.SerializerCode;

import java.io.IOException;

/**
 * 使用JSON格式的序列化器
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
@Slf4j
public class JsonSerializer implements CommonSerializer
{
    private ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(Object obj)
    {
        try
        {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e)
        {
            log.error("序列化时有错误发生: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz)
    {
        try
        {
            Object obj = objectMapper.readValue(bytes, clazz);
            //在 RpcRequest 反序列化时，由于其中有一个字段是 Object 数组，在反序列化时序列化器会根据字段类型进行反序列化，而 Object 就是一个十分模糊的类型，会出现反序列化失败的现象，
            // 这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到 Object 数组中的每个实例的实际类，辅助反序列化，这就是 handleRequest() 方法的作用。
            //上面提到的这种情况不会在其他序列化方式中出现，因为其他序列化方式是转换成字节数组，会记录对象的信息，而 JSON 方式本质上只是转换成 JSON 字符串，会丢失对象的类型信息。
            if(obj instanceof RpcRequest)obj=handleRequest(obj);
            return obj;
        } catch (IOException e)
        {
            log.error("反序列化时有错误发生: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型,需要重新判断处理
     * @param obj
     * @return {@link Object }
     */
    private Object handleRequest(Object obj) throws IOException
    {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for(int i = 0; i < rpcRequest.getParameterTypes().length; i ++)
        {
            Class<?> parameterType=rpcRequest.getParameterTypes()[i];
            //判断实际的参数类型于反序列化的参数类型是否一致,不一致则改为一致
            if(!parameterType.isAssignableFrom(rpcRequest.getParameters()[i].getClass()))
            {
                byte[] bytes= objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i]=objectMapper.readValue(bytes,parameterType);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode()
    {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
