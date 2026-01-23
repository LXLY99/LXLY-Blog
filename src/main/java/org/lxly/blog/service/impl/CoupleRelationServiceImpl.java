package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.lxly.blog.common.BizException;
import org.lxly.blog.entity.CoupleRelation;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.mapper.CoupleRelationMapper;
import org.lxly.blog.service.CoupleRelationService;
import org.lxly.blog.service.SysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoupleRelationServiceImpl extends ServiceImpl<CoupleRelationMapper, CoupleRelation> implements CoupleRelationService {
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_REJECTED = "REJECTED";
    private static final String STATUS_ENDED = "ENDED";

    private final SysUserService userService;

    @Override
    public CoupleRelation sendInvite(Long requesterId, Long responderId) {
        if (requesterId == null || responderId == null) {
            throw BizException.badRequest("Invalid user");
        }
        if (requesterId.equals(responderId)) {
            throw BizException.badRequest("Cannot invite yourself");
        }
        SysUser target = userService.getById(responderId);
        if (target == null) {
            throw BizException.badRequest("User not found");
        }
        if (findActiveRelation(requesterId).isPresent()) {
            throw BizException.badRequest("You already have an active relation");
        }
        if (findActiveRelation(responderId).isPresent()) {
            throw BizException.badRequest("Target already has an active relation");
        }
        CoupleRelation existingPending = lambdaQuery()
                .eq(CoupleRelation::getStatus, STATUS_PENDING)
                .and(w -> w.eq(CoupleRelation::getRequesterId, requesterId)
                        .eq(CoupleRelation::getResponderId, responderId)
                        .or()
                        .eq(CoupleRelation::getRequesterId, responderId)
                        .eq(CoupleRelation::getResponderId, requesterId))
                .last("LIMIT 1")
                .one();
        if (existingPending != null) {
            throw BizException.badRequest("Pending invite already exists");
        }
        CoupleRelation relation = new CoupleRelation();
        relation.setRequesterId(requesterId);
        relation.setResponderId(responderId);
        relation.setStatus(STATUS_PENDING);
        relation.setCreateTime(LocalDateTime.now());
        relation.setUpdateTime(LocalDateTime.now());
        save(relation);
        return relation;
    }

    @Override
    public CoupleRelation acceptInvite(Long relationId, Long userId) {
        CoupleRelation relation = getById(relationId);
        if (relation == null || !STATUS_PENDING.equals(relation.getStatus())) {
            throw BizException.badRequest("Invite not found");
        }
        if (!relation.getResponderId().equals(userId)) {
            throw BizException.forbidden("Not your invite");
        }
        if (findActiveRelation(userId).isPresent()) {
            throw BizException.badRequest("You already have an active relation");
        }
        if (findActiveRelation(relation.getRequesterId()).isPresent()) {
            throw BizException.badRequest("Requester already has an active relation");
        }
        relation.setStatus(STATUS_ACTIVE);
        relation.setConfirmTime(LocalDateTime.now());
        relation.setUpdateTime(LocalDateTime.now());
        updateById(relation);
        return relation;
    }

    @Override
    public CoupleRelation rejectInvite(Long relationId, Long userId) {
        CoupleRelation relation = getById(relationId);
        if (relation == null || !STATUS_PENDING.equals(relation.getStatus())) {
            throw BizException.badRequest("Invite not found");
        }
        if (!relation.getResponderId().equals(userId)) {
            throw BizException.forbidden("Not your invite");
        }
        relation.setStatus(STATUS_REJECTED);
        relation.setUpdateTime(LocalDateTime.now());
        updateById(relation);
        return relation;
    }

    @Override
    public CoupleRelation breakup(Long userId) {
        CoupleRelation relation = findActiveRelation(userId)
                .orElseThrow(() -> BizException.badRequest("No active relation"));
        relation.setStatus(STATUS_ENDED);
        relation.setUpdateTime(LocalDateTime.now());
        updateById(relation);
        return relation;
    }

    @Override
    public Optional<CoupleRelation> findActiveRelation(Long userId) {
        return Optional.ofNullable(lambdaQuery()
                .eq(CoupleRelation::getStatus, STATUS_ACTIVE)
                .and(w -> w.eq(CoupleRelation::getRequesterId, userId)
                        .or()
                        .eq(CoupleRelation::getResponderId, userId))
                .last("LIMIT 1")
                .one());
    }

    @Override
    public List<CoupleRelation> pendingInvites(Long userId) {
        return lambdaQuery()
                .eq(CoupleRelation::getStatus, STATUS_PENDING)
                .eq(CoupleRelation::getResponderId, userId)
                .orderByDesc(CoupleRelation::getCreateTime)
                .list();
    }
}
