package org.lxly.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class ArticleDtos {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String title;
        private String summary;
        @NotBlank
        private String content; // markdown
        private Long categoryId;
        private String coverImage;
        private String coverDeleteHash;
        private Integer status; // 1 published, 0 draft
        private Integer isTop;
        private LocalDateTime createTime; // optional override
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String summary;
        private String content;
        private Long categoryId;
        private String coverImage;
        private String coverDeleteHash;
        private Integer status;
        private Integer isTop;
    }
}
