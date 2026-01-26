package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lxly.blog.entity.CoupleCalendarEvent;
import org.lxly.blog.mapper.CoupleCalendarEventMapper;
import org.lxly.blog.service.CoupleCalendarEventService;
import org.springframework.stereotype.Service;

@Service
public class CoupleCalendarEventServiceImpl extends ServiceImpl<CoupleCalendarEventMapper, CoupleCalendarEvent> implements CoupleCalendarEventService {
}
