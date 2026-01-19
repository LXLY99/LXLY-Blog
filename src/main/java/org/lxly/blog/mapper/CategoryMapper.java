package org.lxly.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lxly.blog.entity.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
