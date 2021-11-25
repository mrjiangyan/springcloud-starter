package com.touchbiz.starter.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.touchbiz.common.entity.query.BaseQuery;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AAA {

    @NotNull(message = "233222")
    private String data;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
}
