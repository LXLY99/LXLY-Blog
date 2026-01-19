package org.lxly.blog.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.redis.RedisKeys;
import org.lxly.blog.service.SysUserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSequenceInitializer implements ApplicationRunner {
    private final SysUserService userService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        // Determine current max suffix among LXLY_***
        List<SysUser> users = userService.lambdaQuery()
                .select(SysUser::getSystemName)
                .likeRight(SysUser::getSystemName, "LXLY_")
                .list();

        long max = -1;
        for (SysUser u : users) {
            String s = u.getSystemName();
            if (s == null) continue;
            int idx = s.indexOf('_');
            if (idx < 0 || idx + 1 >= s.length()) continue;
            String tail = s.substring(idx + 1);
            try {
                long v = Long.parseLong(tail);
                if (v > max) max = v;
            } catch (NumberFormatException ignored) {
            }
        }

        long expectedSeq = max + 1; // next index
        String curStr = redisTemplate.opsForValue().get(RedisKeys.SEQ_USER_SYSTEM_NAME);
        Long cur = null;
        try {
            if (curStr != null) cur = Long.parseLong(curStr);
        } catch (NumberFormatException ignored) {
        }

        // Remember: seq stored is (nextIndex + 1) because generator does seq-1
        long stored = expectedSeq + 1;
        if (cur == null || cur < stored) {
            redisTemplate.opsForValue().set(RedisKeys.SEQ_USER_SYSTEM_NAME, String.valueOf(stored));
            log.info("Initialized Redis user system_name seq to {} (max existing index={})", stored, max);
        } else {
            log.info("Redis user system_name seq already {} (max existing index={})", cur, max);
        }
    }
}
