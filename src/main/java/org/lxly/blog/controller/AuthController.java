package org.lxly.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.dto.AuthDtos;
import org.lxly.blog.service.AuthService;
import org.lxly.blog.util.HttpUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/code/register")
    public ApiResponse<Void> sendRegisterCode(@Valid @RequestBody AuthDtos.SendCodeRequest req, HttpServletRequest request) {
        authService.sendRegisterCode(req.getEmail(), HttpUtil.getClientIp(request));
        return ApiResponse.ok();
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, String>> register(@Valid @RequestBody AuthDtos.RegisterRequest req, HttpServletRequest request) {
        String token = authService.register(req.getEmail(), req.getCode(), req.getPassword(), req.getNickname(), HttpUtil.getClientIp(request));
        return ApiResponse.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody AuthDtos.LoginRequest req, HttpServletRequest request) {
        String token = authService.login(req.getEmail(), req.getPassword(), HttpUtil.getClientIp(request));
        return ApiResponse.ok(Map.of("token", token));
    }

    @PostMapping("/code/reset-password")
    public ApiResponse<Void> sendResetPasswordCode(@Valid @RequestBody AuthDtos.SendCodeRequest req, HttpServletRequest request) {
        authService.sendResetPasswordCode(req.getEmail(), HttpUtil.getClientIp(request));
        return ApiResponse.ok();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody AuthDtos.ResetPasswordRequest req, HttpServletRequest request) {
        authService.resetPassword(req.getEmail(), req.getCode(), req.getNewPassword(), HttpUtil.getClientIp(request));
        return ApiResponse.ok();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null) {
            return ApiResponse.ok();
        }
        String token = authHeader.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        authService.logout(token);
        return ApiResponse.ok();
    }
}
