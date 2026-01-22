package org.lxly.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxly.blog.entity.CoupleRelation;

import java.util.List;
import java.util.Optional;

public interface CoupleRelationService extends IService<CoupleRelation> {
    CoupleRelation sendInvite(Long requesterId, Long responderId);
    CoupleRelation acceptInvite(Long relationId, Long userId);
    CoupleRelation rejectInvite(Long relationId, Long userId);
    CoupleRelation breakup(Long userId);
    Optional<CoupleRelation> findActiveRelation(Long userId);
    List<CoupleRelation> pendingInvites(Long userId);
}
