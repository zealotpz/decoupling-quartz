package com.zealotpz.quartz.job;

import com.zealotpz.quartz.model.QuartzJob;
import com.zealotpz.quartz.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * description:
 * create: 2020-01-08 11:10
 **/

@Slf4j
@Component
public class JobUtil {

    //任务执行时间校验
    public static boolean jobExecuteTimeCheck(JobService jobService, JobKey jobkey, QuartzJob job) {
        //开始时间大于当前时间 => 执行时间未到
        if (null != job.getJobEffectDateStart() && job.getJobEffectDateStart().isAfter(LocalDateTime.now())) {
            log.info("QueenJob 任务:[{}]-任务组:[{}]-执行时间[{}]未到,此次不执行", jobkey.getName(), jobkey.getGroup(), job.getJobEffectDateStart());
            return true;
        }
        //结束时间小于当前时间 => 执行时间完成,停止任务
        if (null != job.getJobEffectDateEnd() && job.getJobEffectDateEnd().isBefore(LocalDateTime.now())) {
            log.info("QueenJob 任务:[{}]-任务组:[{}]-执行时间[{}]已结束,暂停任务", jobkey.getName(), jobkey.getGroup(), job.getJobEffectDateStart());
            // 暂停任务
            jobService.pauseJob(jobkey.getName(), jobkey.getGroup());
            return true;
        }
        return false;
    }
}
