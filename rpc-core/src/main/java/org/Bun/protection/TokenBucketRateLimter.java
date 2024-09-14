package org.Bun.protection;

/**
 * 基于令牌桶算法的限流器
 * 用于保护服务提供方
 * @author 萌萌哒AI
 * @date 2024/09/14
 */
public class TokenBucketRateLimter
{
    private final int capacity;          // 令牌桶容量
    private final int refillRate;     // 令牌桶每秒填充速率
    private int tokens;               // 当前令牌数量
    private long lastRefillTimestamp;    // 上次填充令牌的时间戳

    public TokenBucketRateLimter(int capacity, int refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;  // 每秒添加几个令牌
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public boolean allowRequest() {
        synchronized (this) {
            refillTokens();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }

    private void refillTokens() {

        long now = System.currentTimeMillis();
        long elapsedTime = (now - lastRefillTimestamp) / 1000; // 转换为秒
//            大于一秒进行添加
        if (elapsedTime > 1) {
            double tokensToAdd = elapsedTime * refillRate;
            tokens = (int) Math.min(tokens + tokensToAdd, capacity);
            lastRefillTimestamp = now;
        }
    }




}
