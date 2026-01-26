package org.lxly.blog.controller;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.auth.UserContextHolder;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.dto.CoupleDtos;
import org.lxly.blog.entity.CoupleRelation;
import org.lxly.blog.service.CoupleRelationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/couple")
@RequiredArgsConstructor
public class CoupleController {
    private final CoupleRelationService coupleRelationService;

    @LoginRequired
    @PostMapping("/invite")
    public ApiResponse<CoupleRelation> invite(@RequestBody @jakarta.validation.Valid CoupleDtos.InviteRequest req) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(coupleRelationService.sendInvite(userId, req.getTargetUserId()));
    }

    @LoginRequired
    @PostMapping("/accept")
    public ApiResponse<CoupleRelation> accept(@RequestBody @jakarta.validation.Valid CoupleDtos.RelationActionRequest req) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(coupleRelationService.acceptInvite(req.getRelationId(), userId));
    }

    @LoginRequired
    @PostMapping("/reject")
    public ApiResponse<CoupleRelation> reject(@RequestBody @jakarta.validation.Valid CoupleDtos.RelationActionRequest req) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(coupleRelationService.rejectInvite(req.getRelationId(), userId));
    }

    @LoginRequired
    @PostMapping("/breakup")
    public ApiResponse<CoupleRelation> breakup() {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(coupleRelationService.breakup(userId));
    }

    @LoginRequired
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> status() {
        Long userId = UserContextHolder.getUserId();
        List<CoupleRelation> pending = coupleRelationService.pendingInvites(userId);
        CoupleRelation active = coupleRelationService.findActiveRelation(userId).orElse(null);
        return ApiResponse.ok(Map.of(
                "active", active,
                "pendingInvites", pending
        ));
    }
}
