package org.lxly.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;
    private String title;
    private String summary;
    private String content;
    private String contentHtml;
    private String coverImage;
    private String coverDeleteHash;
    private Integer viewCount;
    private Integer isTop;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
