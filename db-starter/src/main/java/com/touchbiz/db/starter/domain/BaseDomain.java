package com.touchbiz.db.starter.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseDomain {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Boolean deleted;

    private Long creator;

    private Long modifier;

    private Timestamp gmtCreate;

    private Timestamp gmtModify;

    private Boolean status;

}

