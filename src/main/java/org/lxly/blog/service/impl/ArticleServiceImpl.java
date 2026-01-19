package org.lxly.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.lxly.blog.common.BizException;
import org.lxly.blog.entity.Article;
import org.lxly.blog.mapper.ArticleMapper;
import org.lxly.blog.service.ArticleService;
import org.lxly.blog.util.MarkdownUtil;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public Article createArticle(Article article) {
        if (article.getContentHtml() == null) {
            article.setContentHtml(MarkdownUtil.toHtml(article.getContent()));
        }
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        save(article);
        return article;
    }

    @Override
    public Article updateOwnArticle(Long articleId, Article patch) {
        Article db = getById(articleId);
        if (db == null) {
            throw BizException.badRequest("Article not found");
        }
        if (!db.getUserId().equals(patch.getUserId())) {
            throw BizException.forbidden("Not your article");
        }

        // apply patch fields (only a small set)
        if (patch.getTitle() != null) db.setTitle(patch.getTitle());
        if (patch.getSummary() != null) db.setSummary(patch.getSummary());
        if (patch.getContent() != null) {
            db.setContent(patch.getContent());
            db.setContentHtml(MarkdownUtil.toHtml(patch.getContent()));
        }
        if (patch.getCoverImage() != null) db.setCoverImage(patch.getCoverImage());
        if (patch.getCoverDeleteHash() != null) db.setCoverDeleteHash(patch.getCoverDeleteHash());
        if (patch.getCategoryId() != null) db.setCategoryId(patch.getCategoryId());
        if (patch.getStatus() != null) db.setStatus(patch.getStatus());
        if (patch.getIsTop() != null) db.setIsTop(patch.getIsTop());

        updateById(db);
        return db;
    }

    @Override
    public void deleteOwnArticle(Long articleId) {
        removeById(articleId);
    }

    @Override
    public Page<Article> listMyArticles(Page<Article> page, Long userId, Integer status, String q) {
        return lambdaQuery()
                .eq(Article::getUserId, userId)
                .eq(status != null, Article::getStatus, status)
                .and(q != null && !q.isBlank(), w -> w.like(Article::getTitle, q).or().like(Article::getContent, q))
                .orderByDesc(Article::getIsTop)
                .orderByDesc(Article::getCreateTime)
                .page(page);
    }

    @Override
    public Article getMyArticle(Long articleId, Long userId) {
        Article a = getById(articleId);
        if (a == null) {
            throw BizException.badRequest("Article not found");
        }
        if (!a.getUserId().equals(userId)) {
            throw BizException.forbidden("Not your article");
        }
        // view count can be incremented
        a.setViewCount(a.getViewCount() == null ? 1 : a.getViewCount() + 1);
        updateById(a);
        return a;
    }

    @Override
    public Map<String, Long> archiveCounts(Long userId) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        List<Article> list = lambdaQuery()
                .select(Article::getCreateTime)
                .eq(Article::getUserId, userId)
                .eq(Article::getStatus, 1)
                .list();
        return list.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(a -> a.getCreateTime().format(fmt), Collectors.counting()));
    }

    @Override
    public List<Article> recentPublished(Long userId, int size) {
        return lambdaQuery()
                .eq(Article::getUserId, userId)
                .eq(Article::getStatus, 1)
                .orderByDesc(Article::getCreateTime)
                .last("LIMIT " + Math.max(1, size))
                .list();
    }
}
