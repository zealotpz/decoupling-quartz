package com.zealotpz.quartz.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * description:
 * author: zealotpz
 * create: 2019-05-13 10:00
 **/
@Data
public class QuartzJobDTO {

    private String jobName;//任务名称

    private String jobGroup;//任务分组名称

    private String description;//任务描述

    private String jobClassName;//执行类

    private String cronExpression;//执行时间

    private String triggerName;//触发器名称

    private String triggerState;//任务状态

    private String oldJobName;//任务名称 用于修改

    private String oldJobGroup;//任务分组 用于修改

    private Integer pageNum;//分页 页数
    private Integer pageSize;//分页 每页数据大小

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime jobEffectDateStart; //任务开始时间

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime jobEffectDateEnd; //任务结束时间

    @Column(name = "job_message")
    private JSONObject jobMessage;//任务消息,JSON 格式

    public Integer getPageNum() {
        return pageNum == null || pageNum == 0 ? 1 : pageNum;
    }

    public Integer getPageSize() {
        return pageSize == null || pageSize == 0 ? 10 : pageSize;
    }

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModify;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
