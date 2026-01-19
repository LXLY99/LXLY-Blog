package org.lxly.blog.service.impl;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.TokenService;
import org.lxly.blog.auth.UserSystemNameGenerator;
import org.lxly.blog.common.BizException;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.mail.MailService;
import org.lxly.blog.redis.RedisKeys;
import org.lxly.blog.redis.RedisRateLimiter;
import org.lxly.blog.service.AuthService;
import org.lxly.blog.service.SysUserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final RedisRateLimiter rateLimiter;
    private final MailService mailService;
    private final TokenService tokenService;
    private final UserSystemNameGenerator systemNameGenerator;

    private static final Duration VC_TTL = Duration.ofMinutes(10);

    @Override
    public void sendRegisterCode(String email, String ip) {
        rateLimitVerifyCode(email, ip);
        String code = genCode();
        redisTemplate.opsForValue().set(vcKey("REGISTER", email), code, VC_TTL);
        mailService.sendVerifyCode(email, "REGISTER", code);
    }

    @Override
    public void sendResetPasswordCode(String email, String ip) {
        rateLimitVerifyCode(email, ip);
        // user must exist
        SysUser user = userService.lambdaQuery().eq(SysUser::getEmail, email).one();
        if (user == null) {
            throw BizException.badRequest("Email not registered");
        }
        String code = genCode();
        redisTemplate.opsForValue().set(vcKey("RESET", email), code, VC_TTL);
        mailService.sendVerifyCode(email, "RESET_PASSWORD", code);
    }

    @Override
    public String register(String email, String code, String rawPassword, String nickname, String ip) {
        // verify code
        String key = vcKey("REGISTER", email);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null || !cached.equals(code)) {
            throw BizException.badRequest("Invalid or expired verification code");
        }
        redisTemplate.delete(key);

        if (userService.lambdaQuery().eq(SysUser::getEmail, email).exists()) {
            throw BizException.badRequest("Email already registered");
        }

        SysUser user = new SysUser();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");
        user.setStatus(1);
        user.setSystemName(systemNameGenerator.nextSystemName());
        user.setNickname((nickname == null || nickname.isBlank()) ? user.getSystemName() : nickname);
        user.setGender(0);
        // avatar default defined in DB default; keep null to let DB default work.
        userService.save(user);

        return tokenService.issueToken(user.getId());
    }

    @Override
    public String login(String email, String rawPassword, String ip) {
        // simple login rate limit per ip
        String ipKey = RedisKeys.RL_PREFIX + "login:ip:" + ip;
        if (!rateLimiter.allow(ipKey, 20, Duration.ofMinutes(1))) {
            throw new BizException(429, "Too many login attempts");
        }

        SysUser user = userService.lambdaQuery().eq(SysUser::getEmail, email).one();
        if (user == null) {
            throw BizException.badRequest("Invalid email or password");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw BizException.forbidden("Account disabled");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw BizException.badRequest("Invalid email or password");
        }
        return tokenService.issueToken(user.getId());
    }

    @Override
    public void resetPassword(String email, String code, String newPassword, String ip) {
        String key = vcKey("RESET", email);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached == null || !cached.equals(code)) {
            throw BizException.badRequest("Invalid or expired verification code");
        }
        redisTemplate.delete(key);

        SysUser user = userService.lambdaQuery().eq(SysUser::getEmail, email).one();
        if (user == null) {
            throw BizException.badRequest("Email not registered");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        tokenService.invalidateByUserId(user.getId());
    }

    @Override
    public void logout(String token) {
        tokenService.invalidate(token);
    }

    private void rateLimitVerifyCode(String email, String ip) {
        String ipKey = RedisKeys.RL_PREFIX + "vc:ip:" + ip;
        if (!rateLimiter.allow(ipKey, 5, Duration.ofMinutes(1))) {
            throw new BizException(429, "Too many requests");
        }
        String emailKey = RedisKeys.RL_PREFIX + "vc:email:" + email;
        if (!rateLimiter.allow(emailKey, 1, Duration.ofMinutes(1))) {
            throw new BizException(429, "Please wait before requesting another code");
        }
        String dailyKey = RedisKeys.RL_PREFIX + "vc:email:day:" + email;
        if (!rateLimiter.allow(dailyKey, 10, Duration.ofDays(1))) {
            throw new BizException(429, "Daily limit reached");
        }
    }

    private String vcKey(String type, String email) {
        return RedisKeys.VC_PREFIX + type + ":" + email.toLowerCase();
    }

    private String genCode() {
        int n = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(n);
    }
}
