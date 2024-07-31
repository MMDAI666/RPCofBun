package org.Bun.netty.serializer;

/**
 * 通用的序列化反序列化接口
 * @author 萌萌哒AI
 * @date 2024/07/30
 */
public interface CommonSerializer
{
    byte[] serialize(Object obj);
    Object deserialize(byte[] bytes, Class<?> clazz);

    //这个方法返回一个整数代码，用于标识具体的序列化实现。
    int getCode();

    //这个静态方法根据传入的代码 code 返回相应的 CommonSerializer 实现。如果代码是 1，则返回 JsonSerializer 的实例；否则返回 null。
    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
