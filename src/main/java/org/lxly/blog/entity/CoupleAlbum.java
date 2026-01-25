package org.lxly.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("couple_album")
public class CoupleAlbum {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private String name;
    private String coverUrl;
    private String coverDeleteHash;
    private String description;
    private LocalDateTime createTime;
}
