package org.lxly.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class UserDtos {

    @Data
    public static class ProfileUpdateRequest {
        private String nickname;
        private Integer gender;
        private String avatar;
        private String avatarDeleteHash;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String oldPassword;
        @NotBlank
        private String newPassword;
    }
}
