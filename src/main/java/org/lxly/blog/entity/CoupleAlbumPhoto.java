package org.lxly.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("couple_album_photo")
public class CoupleAlbumPhoto {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private Long albumId;
    private String url;
    private String note;
    private LocalDateTime createTime;
}
