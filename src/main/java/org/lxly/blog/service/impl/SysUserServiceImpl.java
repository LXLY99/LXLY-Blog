package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.lxly.blog.entity.SysUser;
import org.lxly.blog.mapper.SysUserMapper;
import org.lxly.blog.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getAdminUser() {
        return lambdaQuery().eq(SysUser::getRole, "ADMIN").last("LIMIT 1").one();
    }
}
