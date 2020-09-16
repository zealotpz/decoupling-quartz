package com.zealotpz.quartz.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(name = "t_quartz_job")
public class QuartzJob {

    private Integer id;

    @Column(name = "job_name")
    private String jobName;//任务名称

    @Column(name = "job_group")
    private String jobGroup;//任务分组名称

    @Column(name = "description")
    private String description;//任务描述

    @Column(name = "job_class_name")
    private String jobClassName;//执行类

    @Column(name = "cron_expression")
    private String cronExpression;//执行时间

    @Column(name = "trigger_name")
    private String triggerName;//触发器名称

    @Column(name = "trigger_state")
    private String triggerState;//任务状态

    @Column(name = "old_job_name")
    private String oldJobName;//任务名称 用于修改

    @Column(name = "old_job_group")
    private String oldJobGroup;//任务分组 用于修改

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "job_effect_date_start")
    private LocalDateTime jobEffectDateStart; //任务开始时间

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "job_effect_date_end")
    private LocalDateTime jobEffectDateEnd; //任务结束时间

//    @ColumnType(typeHandler=AddressTypeHandler.class)
    @Column(name = "job_message")
    private JSONObject jobMessage;//任务消息,JSON 格式

    /**
     * 创建时间(注册时间)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modify")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModify;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
