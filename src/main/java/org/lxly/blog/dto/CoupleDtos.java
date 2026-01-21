package org.lxly.blog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CoupleDtos {
    @Data
    public static class InviteRequest {
        @NotNull(message = "Target user required")
        private Long targetUserId;
    }

    @Data
    public static class RelationActionRequest {
        @NotNull(message = "Relation id required")
        private Long relationId;
    }

    @Data
    public static class AlbumCreateRequest {
        @NotNull(message = "Album name required")
        private String name;
        private String coverUrl;
        private String description;
    }

    @Data
    public static class AlbumPhotoCreateRequest {
        @NotNull(message = "Album id required")
        private Long albumId;
        @NotNull(message = "Photo url required")
        private String url;
        private String note;
    }

    @Data
    public static class CalendarEventRequest {
        @NotNull(message = "Title required")
        private String title;
        private String description;
        @NotNull(message = "Start time required")
        private java.time.LocalDateTime startTime;
        private java.time.LocalDateTime endTime;
        private Boolean shared;
    }

    @Data
    public static class TodoRequest {
        @NotNull(message = "Content required")
        private String content;
        private java.time.LocalDateTime dueTime;
    }

    @Data
    public static class MessageRequest {
        @NotNull(message = "Content required")
        private String content;
    }

    @Data
    public static class MilestoneRequest {
        @NotNull(message = "Title required")
        private String title;
        private String description;
        @NotNull(message = "Event date required")
        private java.time.LocalDate eventDate;
    }

    @Data
    public static class ImportantDateRequest {
        @NotNull(message = "Title required")
        private String title;
        @NotNull(message = "Date required")
        private java.time.LocalDate date;
        private Integer remindDays;
    }
}
