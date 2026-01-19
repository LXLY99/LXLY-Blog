package org.lxly.blog.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {
    private final StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> INCR_EXPIRE_SCRIPT = new DefaultRedisScript<>(
            "local current = redis.call('INCR', KEYS[1]); " +
            "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; " +
            "return current;",
            Long.class
    );

    /**
     * Fixed window rate limiting.
     * @return true if allowed.
     */
    public boolean allow(String key, int limit, Duration window) {
        Long current = redisTemplate.execute(INCR_EXPIRE_SCRIPT, List.of(key), String.valueOf(window.getSeconds()));
        return current != null && current <= limit;
    }
}
