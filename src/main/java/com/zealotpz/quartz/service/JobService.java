package com.zealotpz.quartz.service;

import com.github.pagehelper.PageInfo;
import com.zealotpz.quartz.dto.QuartzJobDTO;
import com.zealotpz.quartz.entity.Result;
import com.zealotpz.quartz.model.QuartzJob;

public interface JobService {

    PageInfo<QuartzJob> listQuartzJob(String jobName, Integer pageNo, Integer pageSize);

    /**
     * 新增job
     *
     * @param quartz
     * @return
     */
    Result saveJob(QuartzJob quartz);

    /**
     * 触发job
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result<String> triggerJob(String jobName, String jobGroup);

    /**
     * 获取任务状态
     * NONE: 不存在
     * NORMAL: 正常
     * PAUSED: 暂停
     * COMPLETE:完成
     * ERROR : 错误
     * BLOCKED : 阻塞
     *
     * @param jobName
     * @return
     */
    Result<String> getTriggerState(String jobName, String jobGroup);

    Result<String> triggerJobOnce(String jobName, String jobGroup);

    /**
     * 暂停job
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result<String> pauseJob(String jobName, String jobGroup);

    /**
     * 恢复job
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result<String> resumeJob(String jobName, String jobGroup);

    Result<String> modifyJobTime(String jobName, String jobGroup, String cronExpression);

    /**
     * 移除job
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result<String> removeJob(String jobName, String jobGroup);

    Result<QuartzJob> getJob(String jobName, String jobGroup);

    /**
     * 修改任务，（可以修改任务名，任务类，触发时间）
     * 原理：移除原来的任务，添加新的任务
     *
     * @param quartzJobDTO
     * @return
     */
    Result<String> updateJob(QuartzJobDTO quartzJobDTO);

    /**
     * 修改任务，（仅修改 描述,起止时间）
     *
     * @param quartzJob
     * @return
     */
    Result<String> updateJobBase(QuartzJob quartzJob);


    void schedulerJob(QuartzJob job) throws Exception;
}