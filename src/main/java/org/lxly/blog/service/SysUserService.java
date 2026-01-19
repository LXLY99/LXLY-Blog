package org.lxly.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxly.blog.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getAdminUser();
}
