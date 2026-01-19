package org.lxly.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lxly.blog.entity.Article;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
