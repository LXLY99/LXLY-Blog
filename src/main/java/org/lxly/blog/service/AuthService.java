package org.lxly.blog.service;

import org.lxly.blog.entity.SysUser;

public interface AuthService {
    void sendRegisterCode(String email, String ip);
    void sendResetPasswordCode(String email, String ip);

    /**
     * Register and return token.
     */
    String register(String email, String code, String rawPassword, String nickname, String ip);

    /**
     * Login and return token.
     */
    String login(String email, String rawPassword, String ip);

    void resetPassword(String email, String code, String newPassword, String ip);

    void logout(String token);
}
