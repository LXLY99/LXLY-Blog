package org.lxly.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CommentDtos {
    @Data
    public static class CreateRequest {
        @NotNull
        private Long articleId;
        @NotBlank
        private String content;
        private Long parentId;
    }
}
