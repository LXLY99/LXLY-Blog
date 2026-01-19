package org.lxly.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.lxly.blog.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    Comment addComment(Comment comment);
    List<Comment> listByArticle(Long articleId);
}
