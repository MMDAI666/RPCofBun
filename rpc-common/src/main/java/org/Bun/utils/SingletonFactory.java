package org.Bun.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 * @author 萌萌哒AI
 * @date 2024/09/12
 */
public class SingletonFactory
{
    private static Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {};

    public static <T>T getInstance(Class<T> clazz)
    {
        Object instance  = objectMap.get(clazz);
        synchronized(clazz)
        {
            if(instance == null)
            {
                try
                {
                    instance=clazz.newInstance();
                    objectMap.put(clazz, instance);
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }
        }
        return clazz.cast(instance);
    }
}
