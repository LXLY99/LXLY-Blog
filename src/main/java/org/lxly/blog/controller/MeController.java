package org.lxly.blog.controller;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.auth.TokenService;
import org.lxly.blog.auth.UserContextHolder;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.common.BizException;
import org.lxly.blog.dto.UserDtos;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.service.SysUserService;
import org.lxly.blog.smms.SmmsClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {
    private final SysUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final SmmsClient smmsClient;

    @LoginRequired
    @GetMapping
    public ApiResponse<Map<String, Object>> me() {
        Long userId = UserContextHolder.getUserId();
        SysUser u = userService.getById(userId);
        if (u == null) {
            throw BizException.unauthorized("Not logged in");
        }
        return ApiResponse.ok(Map.of(
                "id", u.getId(),
                "systemName", u.getSystemName(),
                "nickname", u.getNickname(),
                "email", u.getEmail(),
                "avatar", u.getAvatar(),
                "gender", u.getGender(),
                "role", u.getRole()
        ));
    }

    @LoginRequired
    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(@RequestBody @jakarta.validation.Valid UserDtos.ProfileUpdateRequest req) {
        Long userId = UserContextHolder.getUserId();
        SysUser u = userService.getById(userId);
        if (u == null) {
            throw BizException.unauthorized("Not logged in");
        }

        if (req.getNickname() != null) u.setNickname(req.getNickname());
        if (req.getGender() != null) u.setGender(req.getGender());

        // avatar update + delete old
        if (req.getAvatar() != null) {
            String oldHash = u.getAvatarDeleteHash();
            u.setAvatar(req.getAvatar());
            u.setAvatarDeleteHash(req.getAvatarDeleteHash());
            // try delete old asynchronously is better; here we just fire and ignore failures
            smmsClient.deleteByHash(oldHash);
        }

        userService.updateById(u);
        return ApiResponse.ok();
    }

    @LoginRequired
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@RequestBody @jakarta.validation.Valid UserDtos.ChangePasswordRequest req,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = UserContextHolder.getUserId();
        SysUser u = userService.getById(userId);
        if (u == null) {
            throw BizException.unauthorized("Not logged in");
        }
        if (!passwordEncoder.matches(req.getOldPassword(), u.getPassword())) {
            throw BizException.badRequest("Old password incorrect");
        }
        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userService.updateById(u);

        // force re-login
        tokenService.invalidateByUserId(userId);
        if (authHeader != null) {
            String token = authHeader.trim();
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7).trim();
            }
            tokenService.invalidate(token);
        }
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null) {
            return ApiResponse.ok();
        }
        String token = authHeader.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        tokenService.invalidate(token);
        return ApiResponse.ok();
    }
}
