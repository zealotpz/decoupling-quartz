package com.zealotpz.quartz.model;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_job_trigger_log")
public class JobTriggerLog {

    private Integer id;

    @Column(name = "job_name")
    private String jobName;//任务名称

    @Column(name = "description")
    private String description;//任务描述

//    @Column(name = "cron_expression")
//    private String cronExpression;//执行时间

    @Column(name = "trigger_name")
    private String triggerName;//触发器名称

    @Column(name = "trigger_state")
    private String triggerState;//任务触发状态

    /**
     * 创建时间(注册时间)
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 下次触发时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "next_fire_time")
    private Date nextFireTime;

//    /**
//     * 修改时间
//     */
//    @Column(name = "gmt_modify")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private LocalDateTime gmtModify;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
