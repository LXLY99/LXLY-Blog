package org.lxly.blog.auth;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.redis.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSystemNameGenerator {
    private final StringRedisTemplate redisTemplate;

    /**
     * Generate immutable system user id: LXLY_000, LXLY_001, ...
     */
    public String nextSystemName() {
        Long seq = redisTemplate.opsForValue().increment(RedisKeys.SEQ_USER_SYSTEM_NAME);
        if (seq == null) {
            throw new IllegalStateException("Redis INCR failed");
        }
        long idx = seq - 1; // make first = 0
        return "LXLY_" + leftPad3(idx);
    }

    private String leftPad3(long n) {
        if (n < 0) n = 0;
        if (n <= 9) return "00" + n;
        if (n <= 99) return "0" + n;
        return String.valueOf(n);
    }
}
