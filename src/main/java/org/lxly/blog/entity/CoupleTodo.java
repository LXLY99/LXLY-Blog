package org.lxly.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("couple_todo")
public class CoupleTodo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private String content;
    private Boolean completed;
    private LocalDateTime dueTime;
    private LocalDateTime createTime;
}
