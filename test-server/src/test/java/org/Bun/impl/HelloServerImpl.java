package org.Bun.impl;

import lombok.extern.slf4j.Slf4j;

import org.Bun.api.HelloServer;
import org.Bun.domain.HelloObject;


@Slf4j
public class HelloServerImpl implements HelloServer
{

    @Override
    public String hello(HelloObject object) {
        log.info("接收到：{}", object.getMessage());
        return "这是调用的返回值，id=" + object.getId();
    }
}
