package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lxly.blog.entity.CoupleMessage;
import org.lxly.blog.mapper.CoupleMessageMapper;
import org.lxly.blog.service.CoupleMessageService;
import org.springframework.stereotype.Service;

@Service
public class CoupleMessageServiceImpl extends ServiceImpl<CoupleMessageMapper, CoupleMessage> implements CoupleMessageService {
}
