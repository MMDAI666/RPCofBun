package org.Bun.impl;

import lombok.extern.slf4j.Slf4j;
import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;
@Slf4j
public class HelloServerImpl2 implements HelloServer
{

    @Override
    public String hello(HelloObject object) {
        log.info("接收到消息：{}", object.getMessage());
        return "本次处理来自Socket服务";
    }
}
