package org.lxly.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("couple_important_date")
public class CoupleImportantDate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long relationId;
    private String title;
    private LocalDate date;
    private Integer remindDays;
    private LocalDateTime createTime;
}
