package org.lxly.blog.controller;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.auth.UserContextHolder;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.common.BizException;
import org.lxly.blog.dto.CoupleDtos;
import org.lxly.blog.entity.*;
import org.lxly.blog.service.*;
import org.lxly.blog.smms.SmmsClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/couple")
@RequiredArgsConstructor
public class CoupleContentController {
    private final CoupleRelationService coupleRelationService;
    private final CoupleAlbumService albumService;
    private final CoupleAlbumPhotoService albumPhotoService;
    private final CoupleCalendarEventService calendarEventService;
    private final CoupleTodoService todoService;
    private final CoupleMessageService messageService;
    private final CoupleMilestoneService milestoneService;
    private final CoupleImportantDateService importantDateService;
    private final SmmsClient smmsClient;

    private Long activeRelationId() {
        Long userId = UserContextHolder.getUserId();
        return coupleRelationService.findActiveRelation(userId)
                .map(CoupleRelation::getId)
                .orElseThrow(() -> BizException.badRequest("No active relation"));
    }

    @LoginRequired
    @PostMapping("/albums")
    public ApiResponse<CoupleAlbum> createAlbum(@RequestBody @jakarta.validation.Valid CoupleDtos.AlbumCreateRequest req) {
        Long relationId = activeRelationId();
        CoupleAlbum album = new CoupleAlbum();
        album.setRelationId(relationId);
        album.setName(req.getName());
        album.setCoverUrl(req.getCoverUrl());
        album.setCoverDeleteHash(req.getCoverDeleteHash());
        album.setDescription(req.getDescription());
        album.setCreateTime(LocalDateTime.now());
        albumService.save(album);
        return ApiResponse.ok(album);
    }

    @LoginRequired
    @GetMapping("/albums")
    public ApiResponse<List<CoupleAlbum>> listAlbums() {
        Long relationId = activeRelationId();
        return ApiResponse.ok(albumService.lambdaQuery()
                .eq(CoupleAlbum::getRelationId, relationId)
                .orderByDesc(CoupleAlbum::getCreateTime)
                .list());
    }

    @LoginRequired
    @DeleteMapping("/albums/{id}")
    public ApiResponse<Void> deleteAlbum(@PathVariable Long id) {
        Long relationId = activeRelationId();
        CoupleAlbum album = albumService.lambdaQuery()
                .eq(CoupleAlbum::getId, id)
                .eq(CoupleAlbum::getRelationId, relationId)
                .one();
        if (album == null) {
            throw BizException.badRequest("Album not found");
        }
        albumService.removeById(album.getId());
        smmsClient.deleteByHash(album.getCoverDeleteHash());
        List<CoupleAlbumPhoto> photos = albumPhotoService.lambdaQuery()
                .eq(CoupleAlbumPhoto::getRelationId, relationId)
                .eq(CoupleAlbumPhoto::getAlbumId, id)
                .list();
        if (!photos.isEmpty()) {
            for (CoupleAlbumPhoto photo : photos) {
                smmsClient.deleteByHash(photo.getDeleteHash());
            }
            albumPhotoService.removeBatchByIds(photos.stream().map(CoupleAlbumPhoto::getId).toList());
        }
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/albums/photos")
    public ApiResponse<CoupleAlbumPhoto> addPhoto(@RequestBody @jakarta.validation.Valid CoupleDtos.AlbumPhotoCreateRequest req) {
        Long relationId = activeRelationId();
        CoupleAlbum album = albumService.getById(req.getAlbumId());
        if (album == null || !relationId.equals(album.getRelationId())) {
            throw BizException.badRequest("Album not found");
        }
        CoupleAlbumPhoto photo = new CoupleAlbumPhoto();
        photo.setRelationId(relationId);
        photo.setAlbumId(req.getAlbumId());
        photo.setUrl(req.getUrl());
        photo.setDeleteHash(req.getDeleteHash());
        photo.setNote(req.getNote());
        photo.setCreateTime(LocalDateTime.now());
        albumPhotoService.save(photo);
        return ApiResponse.ok(photo);
    }

    @LoginRequired
    @GetMapping("/albums/{albumId}/photos")
    public ApiResponse<List<CoupleAlbumPhoto>> listPhotos(@PathVariable Long albumId) {
        Long relationId = activeRelationId();
        return ApiResponse.ok(albumPhotoService.lambdaQuery()
                .eq(CoupleAlbumPhoto::getRelationId, relationId)
                .eq(CoupleAlbumPhoto::getAlbumId, albumId)
                .orderByDesc(CoupleAlbumPhoto::getCreateTime)
                .list());
    }

    @LoginRequired
    @DeleteMapping("/albums/photos/{id}")
    public ApiResponse<Void> deletePhoto(@PathVariable Long id) {
        Long relationId = activeRelationId();
        CoupleAlbumPhoto photo = albumPhotoService.lambdaQuery()
                .eq(CoupleAlbumPhoto::getId, id)
                .eq(CoupleAlbumPhoto::getRelationId, relationId)
                .one();
        if (photo == null) {
            throw BizException.badRequest("Photo not found");
        }
        albumPhotoService.removeById(photo.getId());
        smmsClient.deleteByHash(photo.getDeleteHash());
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/calendar")
    public ApiResponse<CoupleCalendarEvent> createEvent(@RequestBody @jakarta.validation.Valid CoupleDtos.CalendarEventRequest req) {
        Long relationId = activeRelationId();
        CoupleCalendarEvent event = new CoupleCalendarEvent();
        event.setRelationId(relationId);
        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setStartTime(req.getStartTime());
        event.setEndTime(req.getEndTime());
        event.setShared(req.getShared() == null ? Boolean.TRUE : req.getShared());
        event.setCreateTime(LocalDateTime.now());
        calendarEventService.save(event);
        return ApiResponse.ok(event);
    }

    @LoginRequired
    @GetMapping("/calendar")
    public ApiResponse<List<CoupleCalendarEvent>> listEvents() {
        Long relationId = activeRelationId();
        return ApiResponse.ok(calendarEventService.lambdaQuery()
                .eq(CoupleCalendarEvent::getRelationId, relationId)
                .orderByAsc(CoupleCalendarEvent::getStartTime)
                .list());
    }

    @LoginRequired
    @DeleteMapping("/calendar/{id}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long id) {
        Long relationId = activeRelationId();
        boolean removed = calendarEventService.lambdaUpdate()
                .eq(CoupleCalendarEvent::getId, id)
                .eq(CoupleCalendarEvent::getRelationId, relationId)
                .remove();
        if (!removed) {
            throw BizException.badRequest("Event not found");
        }
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/todos")
    public ApiResponse<CoupleTodo> createTodo(@RequestBody @jakarta.validation.Valid CoupleDtos.TodoRequest req) {
        Long relationId = activeRelationId();
        CoupleTodo todo = new CoupleTodo();
        todo.setRelationId(relationId);
        todo.setContent(req.getContent());
        todo.setDueTime(req.getDueTime());
        todo.setCompleted(Boolean.FALSE);
        todo.setCreateTime(LocalDateTime.now());
        todoService.save(todo);
        return ApiResponse.ok(todo);
    }

    @LoginRequired
    @GetMapping("/todos")
    public ApiResponse<List<CoupleTodo>> listTodos() {
        Long relationId = activeRelationId();
        return ApiResponse.ok(todoService.lambdaQuery()
                .eq(CoupleTodo::getRelationId, relationId)
                .orderByAsc(CoupleTodo::getCompleted)
                .orderByDesc(CoupleTodo::getCreateTime)
                .list());
    }

    @LoginRequired
    @PostMapping("/todos/{id}/toggle")
    public ApiResponse<CoupleTodo> toggleTodo(@PathVariable Long id) {
        Long relationId = activeRelationId();
        CoupleTodo todo = todoService.lambdaQuery()
                .eq(CoupleTodo::getId, id)
                .eq(CoupleTodo::getRelationId, relationId)
                .one();
        if (todo == null) {
            throw BizException.badRequest("Todo not found");
        }
        todo.setCompleted(todo.getCompleted() == null || !todo.getCompleted());
        todoService.updateById(todo);
        return ApiResponse.ok(todo);
    }

    @LoginRequired
    @DeleteMapping("/todos/{id}")
    public ApiResponse<Void> deleteTodo(@PathVariable Long id) {
        Long relationId = activeRelationId();
        boolean removed = todoService.lambdaUpdate()
                .eq(CoupleTodo::getId, id)
                .eq(CoupleTodo::getRelationId, relationId)
                .remove();
        if (!removed) {
            throw BizException.badRequest("Todo not found");
        }
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/messages")
    public ApiResponse<CoupleMessage> createMessage(@RequestBody @jakarta.validation.Valid CoupleDtos.MessageRequest req) {
        Long relationId = activeRelationId();
        Long userId = UserContextHolder.getUserId();
        CoupleMessage message = new CoupleMessage();
        message.setRelationId(relationId);
        message.setUserId(userId);
        message.setContent(req.getContent());
        message.setCreateTime(LocalDateTime.now());
        messageService.save(message);
        return ApiResponse.ok(message);
    }

    @LoginRequired
    @GetMapping("/messages")
    public ApiResponse<List<CoupleMessage>> listMessages() {
        Long relationId = activeRelationId();
        return ApiResponse.ok(messageService.lambdaQuery()
                .eq(CoupleMessage::getRelationId, relationId)
                .orderByDesc(CoupleMessage::getCreateTime)
                .list());
    }

    @LoginRequired
    @DeleteMapping("/messages/{id}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long id) {
        Long relationId = activeRelationId();
        boolean removed = messageService.lambdaUpdate()
                .eq(CoupleMessage::getId, id)
                .eq(CoupleMessage::getRelationId, relationId)
                .remove();
        if (!removed) {
            throw BizException.badRequest("Message not found");
        }
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/milestones")
    public ApiResponse<CoupleMilestone> createMilestone(@RequestBody @jakarta.validation.Valid CoupleDtos.MilestoneRequest req) {
        Long relationId = activeRelationId();
        CoupleMilestone milestone = new CoupleMilestone();
        milestone.setRelationId(relationId);
        milestone.setTitle(req.getTitle());
        milestone.setDescription(req.getDescription());
        milestone.setEventDate(req.getEventDate());
        milestone.setCreateTime(LocalDateTime.now());
        milestoneService.save(milestone);
        return ApiResponse.ok(milestone);
    }

    @LoginRequired
    @GetMapping("/milestones")
    public ApiResponse<List<CoupleMilestone>> listMilestones() {
        Long relationId = activeRelationId();
        return ApiResponse.ok(milestoneService.lambdaQuery()
                .eq(CoupleMilestone::getRelationId, relationId)
                .orderByDesc(CoupleMilestone::getEventDate)
                .list());
    }

    @LoginRequired
    @DeleteMapping("/milestones/{id}")
    public ApiResponse<Void> deleteMilestone(@PathVariable Long id) {
        Long relationId = activeRelationId();
        boolean removed = milestoneService.lambdaUpdate()
                .eq(CoupleMilestone::getId, id)
                .eq(CoupleMilestone::getRelationId, relationId)
                .remove();
        if (!removed) {
            throw BizException.badRequest("Milestone not found");
        }
        return ApiResponse.ok();
    }

    @LoginRequired
    @PostMapping("/important-dates")
    public ApiResponse<CoupleImportantDate> createImportantDate(@RequestBody @jakarta.validation.Valid CoupleDtos.ImportantDateRequest req) {
        Long relationId = activeRelationId();
        CoupleImportantDate date = new CoupleImportantDate();
        date.setRelationId(relationId);
        date.setTitle(req.getTitle());
        date.setDate(req.getDate());
        date.setRemindDays(req.getRemindDays());
        date.setCreateTime(LocalDateTime.now());
        importantDateService.save(date);
        return ApiResponse.ok(date);
    }

    @LoginRequired
    @GetMapping("/important-dates")
    public ApiResponse<List<CoupleImportantDate>> listImportantDates() {
        Long relationId = activeRelationId();
        return ApiResponse.ok(importantDateService.lambdaQuery()
                .eq(CoupleImportantDate::getRelationId, relationId)
                .orderByAsc(CoupleImportantDate::getDate)
                .list());
    }

    @LoginRequired
    @DeleteMapping("/important-dates/{id}")
    public ApiResponse<Void> deleteImportantDate(@PathVariable Long id) {
        Long relationId = activeRelationId();
        boolean removed = importantDateService.lambdaUpdate()
                .eq(CoupleImportantDate::getId, id)
                .eq(CoupleImportantDate::getRelationId, relationId)
                .remove();
        if (!removed) {
            throw BizException.badRequest("Important date not found");
        }
        return ApiResponse.ok();
    }
}
