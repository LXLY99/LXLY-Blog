package org.lxly.blog.controller;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.auth.UserContextHolder;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.common.BizException;
import org.lxly.blog.dto.CommentDtos;
import org.lxly.blog.entity.Article;
import org.lxly.blog.entity.Comment;
import org.lxly.blog.redis.RedisKeys;
import org.lxly.blog.redis.RedisRateLimiter;
import org.lxly.blog.service.ArticleService;
import org.lxly.blog.service.CommentService;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final ArticleService articleService;
    private final RedisRateLimiter rateLimiter;

    @LoginRequired
    @PostMapping
    public ApiResponse<Comment> create(@RequestBody @jakarta.validation.Valid CommentDtos.CreateRequest req) {
        Long userId = UserContextHolder.getUserId();

        // rate limit: 1 comment / 5 seconds
        String key = RedisKeys.RL_PREFIX + "comment:user:" + userId;
        if (!rateLimiter.allow(key, 1, Duration.ofSeconds(5))) {
            throw new BizException(429, "Too many comments");
        }

        Article article = articleService.getById(req.getArticleId());
        if (article == null) {
            throw BizException.badRequest("Article not found");
        }
        // minimal project: can only comment on your own article
        if (!article.getUserId().equals(userId)) {
            throw BizException.forbidden("You can only comment on your own articles");
        }

        Comment c = new Comment();
        c.setArticleId(req.getArticleId());
        c.setUserId(userId);
        c.setContent(req.getContent());
        c.setParentId(req.getParentId() == null ? 0L : req.getParentId());

        return ApiResponse.ok(commentService.addComment(c));
    }

    @LoginRequired
    @GetMapping
    public ApiResponse<List<Comment>> list(@RequestParam Long articleId) {
        Long userId = UserContextHolder.getUserId();
        Article article = articleService.getById(articleId);
        if (article == null) {
            throw BizException.badRequest("Article not found");
        }
        if (!article.getUserId().equals(userId)) {
            throw BizException.forbidden("Not your article");
        }
        return ApiResponse.ok(commentService.listByArticle(articleId));
    }
}
