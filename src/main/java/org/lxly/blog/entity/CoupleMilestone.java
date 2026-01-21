package org.lxly.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("couple_milestone")
public class CoupleMilestone {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private String title;
    private String description;
    private LocalDate eventDate;
    private LocalDateTime createTime;
}
