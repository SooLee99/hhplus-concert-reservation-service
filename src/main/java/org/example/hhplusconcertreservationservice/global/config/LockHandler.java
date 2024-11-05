package org.example.hhplusconcertreservationservice.global.config;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class LockHandler {

    private final RedissonClient redissonClient;
    private static final String REDISSON_KEY_PREFIX = "RLOCK_";
    private static final long WAIT_TIME_MS = 5000L; // 필요한 경우 증가 가능
    private static final long LEASE_TIME_MS = -1L; // 수동으로 해제

    @Autowired
    public LockHandler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <T> Optional<T> runOnLock(String key, Supplier<T> execute) {
        RLock lock = redissonClient.getLock(REDISSON_KEY_PREFIX + key);
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(WAIT_TIME_MS, LEASE_TIME_MS, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                // 락을 획득하지 못하면 요청을 버리고 빈 값을 반환
                return Optional.empty();
            }
            return Optional.ofNullable(execute.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public RedissonClient getRedissonClient() {
        return this.redissonClient;
    }
}
