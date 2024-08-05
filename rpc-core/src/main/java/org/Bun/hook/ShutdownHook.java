package org.Bun.hook;

import lombok.extern.slf4j.Slf4j;
import org.Bun.utils.NacosUtil;
import org.Bun.utils.ThreadPoolFactory;

import java.util.concurrent.ExecutorService;

/**
 * 钩子函数,JVM退出时调用,注销服务
 * @author 萌萌哒AI
 * @date 2024/08/05
 */
@Slf4j
public class ShutdownHook
{
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();//单例模式

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        log.info("关闭后将自动注销所有服务");
        //Runtime 对象是 JVM 虚拟机的运行时环境，调用其 addShutdownHook 方法增加一个钩子函数，创建一个新线程调用 clearRegistry 方法完成注销工作。
        // 这个钩子函数会在 JVM 关闭之前被调用。
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }


}
