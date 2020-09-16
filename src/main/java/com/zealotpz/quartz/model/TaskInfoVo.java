package com.zealotpz.quartz.model;

import lombok.Data;

import java.util.Date;

/**
 * description:
 * author: zealotpz
 * create: 2019-06-13 18:05
 **/
@Data
public class TaskInfoVo {

    private String jobName;
    private String jobGroup;
    private String jobDescription;
    private String jobStatus;
    private String cronExpression;
    private String createTime;

    private Date previousFireTime;
    private Date nextFireTime;

}
