package org.lxly.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.lxly.blog.entity.Article;

import java.util.List;
import java.util.Map;

public interface ArticleService extends IService<Article> {
    Article createArticle(Article article);
    Article updateOwnArticle(Long articleId, Article patch);
    void deleteOwnArticle(Long articleId);

    Page<Article> listMyArticles(Page<Article> page, Long userId, Integer status, String q);
    Article getMyArticle(Long articleId, Long userId);

    /**
     * Archive counts by month: YYYY-MM -> count
     */
    Map<String, Long> archiveCounts(Long userId);

    /**
     * Recent published articles.
     */
    List<Article> recentPublished(Long userId, int size);
}
