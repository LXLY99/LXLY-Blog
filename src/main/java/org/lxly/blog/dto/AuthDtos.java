package org.lxly.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class SendCodeRequest {
        @NotBlank
        @Email
        private String email;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String code;
        @NotBlank
        private String password;
        private String nickname;
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        private String code;
        @NotBlank
        private String newPassword;
    }
}
