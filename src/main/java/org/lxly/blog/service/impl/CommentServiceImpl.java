package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.lxly.blog.common.BizException;
import org.lxly.blog.entity.Article;
import org.lxly.blog.entity.Comment;
import org.lxly.blog.mapper.CommentMapper;
import org.lxly.blog.service.ArticleService;
import org.lxly.blog.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    private final ArticleService articleService;

    @Override
    public Comment addComment(Comment comment) {
        Article article = articleService.getById(comment.getArticleId());
        if (article == null) {
            throw BizException.badRequest("Article not found");
        }
        // This project only supports comments on own articles; enforce in controller too.
        save(comment);
        return comment;
    }

    @Override
    public List<Comment> listByArticle(Long articleId) {
        return lambdaQuery()
                .eq(Comment::getArticleId, articleId)
                .orderByAsc(Comment::getCreateTime)
                .list();
    }
}
