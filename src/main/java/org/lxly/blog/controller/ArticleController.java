package org.lxly.blog.controller;

import lombok.RequiredArgsConstructor;
import org.lxly.blog.auth.LoginRequired;
import org.lxly.blog.auth.UserContextHolder;
import org.lxly.blog.common.ApiResponse;
import org.lxly.blog.common.BizException;
import org.lxly.blog.dto.ArticleDtos;
import org.lxly.blog.entity.Article;
import org.lxly.blog.service.ArticleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @LoginRequired
    @PostMapping
    public ApiResponse<Article> create(@RequestBody @jakarta.validation.Valid ArticleDtos.CreateRequest req) {
        Long userId = UserContextHolder.getUserId();
        Article a = new Article();
        a.setUserId(userId);
        a.setTitle(req.getTitle());
        a.setSummary(req.getSummary());
        a.setContent(req.getContent());
        a.setCategoryId(req.getCategoryId());
        a.setCoverImage(req.getCoverImage());
        a.setCoverDeleteHash(req.getCoverDeleteHash());
        a.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        a.setIsTop(req.getIsTop() == null ? 0 : req.getIsTop());
        if (req.getCreateTime() != null) {
            a.setCreateTime(req.getCreateTime());
        }
        return ApiResponse.ok(articleService.createArticle(a));
    }

    @LoginRequired
    @GetMapping("/{id}")
    public ApiResponse<Article> getMy(@PathVariable Long id) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(articleService.getMyArticle(id, userId));
    }

    @LoginRequired
    @PutMapping("/{id}")
    public ApiResponse<Article> update(@PathVariable Long id, @RequestBody @jakarta.validation.Valid ArticleDtos.UpdateRequest req) {
        Long userId = UserContextHolder.getUserId();
        Article patch = new Article();
        patch.setUserId(userId); // used for ownership check
        patch.setTitle(req.getTitle());
        patch.setSummary(req.getSummary());
        patch.setContent(req.getContent());
        patch.setCategoryId(req.getCategoryId());
        patch.setCoverImage(req.getCoverImage());
        patch.setCoverDeleteHash(req.getCoverDeleteHash());
        patch.setStatus(req.getStatus());
        patch.setIsTop(req.getIsTop());
        return ApiResponse.ok(articleService.updateOwnArticle(id, patch));
    }

    @LoginRequired
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        Long userId = UserContextHolder.getUserId();
        Article a = articleService.getById(id);
        if (a == null) {
            throw BizException.badRequest("Article not found");
        }
        if (!a.getUserId().equals(userId)) {
            throw BizException.forbidden("Not your article");
        }
        articleService.removeById(id);
        return ApiResponse.ok();
    }
}
