package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lxly.blog.entity.CoupleTodo;
import org.lxly.blog.mapper.CoupleTodoMapper;
import org.lxly.blog.service.CoupleTodoService;
import org.springframework.stereotype.Service;

@Service
public class CoupleTodoServiceImpl extends ServiceImpl<CoupleTodoMapper, CoupleTodo> implements CoupleTodoService {
}
