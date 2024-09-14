package org.Bun.protection;

/**
 * 熔断器,用于保护服务调用方，开启状态，半开启状态，关闭状态
 * @author 萌萌哒AI
 * @date 2024/09/14
 */
public class CircuitBreaker
{
    private final int failureThreshold; // 触发熔断的失败阈值
    private final int recoveryTimeout; // 熔断器尝试恢复的时间窗口
    private long lastFailureTimestamp; // 上次失败的时间戳
    private int failureCount; // 连续失败的计数

    public CircuitBreaker(int failureThreshold, int recoveryTimeout) {
        this.failureThreshold = failureThreshold;
        this.recoveryTimeout = recoveryTimeout;
        this.lastFailureTimestamp = 0;
        this.failureCount = 0;
    }

    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        if (failureCount >= failureThreshold) {
            // 判断是否超过恢复时间窗口，如果是则重置熔断器状态
            if (now - lastFailureTimestamp >= recoveryTimeout) {
                reset();
            } else {
                return false; // 处于熔断状态，拒绝请求
            }
        }
        return true; // 允许请求
    }

    private void reset() {
        failureCount = 0;
        lastFailureTimestamp = 0;
    }

    public synchronized void recordFailure() {
        long now = System.currentTimeMillis();
        if (now - lastFailureTimestamp >= recoveryTimeout) {
            // 超过恢复时间窗口，重置计数
            reset();
        }
        failureCount++;
        lastFailureTimestamp = now;
    }
}
