package org.lxly.blog.auth;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.redis.RedisKeys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    // session ttl
    private static final Duration TTL = Duration.ofDays(7);

    public String issueToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String tokenKey = RedisKeys.AUTH_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, String.valueOf(userId), TTL);
        // optional single-session: overwrite user's current token
        String userKey = RedisKeys.AUTH_USER_TOKEN_PREFIX + userId;
        String old = redisTemplate.opsForValue().get(userKey);
        if (old != null && !old.isBlank()) {
            redisTemplate.delete(RedisKeys.AUTH_TOKEN_PREFIX + old);
        }
        redisTemplate.opsForValue().set(userKey, token, TTL);
        return token;
    }

    public Long getUserIdByToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String v = redisTemplate.opsForValue().get(RedisKeys.AUTH_TOKEN_PREFIX + token);
        if (v == null) {
            return null;
        }
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void invalidate(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        Long userId = getUserIdByToken(token);
        redisTemplate.delete(RedisKeys.AUTH_TOKEN_PREFIX + token);
        if (userId != null) {
            String userKey = RedisKeys.AUTH_USER_TOKEN_PREFIX + userId;
            String cur = redisTemplate.opsForValue().get(userKey);
            if (token.equals(cur)) {
                redisTemplate.delete(userKey);
            }
        }
    }

    public void invalidateByUserId(Long userId) {
        if (userId == null) {
            return;
        }
        String userKey = RedisKeys.AUTH_USER_TOKEN_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(userKey);
        if (token != null) {
            redisTemplate.delete(RedisKeys.AUTH_TOKEN_PREFIX + token);
        }
        redisTemplate.delete(userKey);
    }
}
