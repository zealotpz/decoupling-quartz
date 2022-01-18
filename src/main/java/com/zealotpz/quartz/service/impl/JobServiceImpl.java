package com.zealotpz.quartz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zealotpz.quartz.config.SimpleTriggerListener;
import com.zealotpz.quartz.dto.QuartzJobDTO;
import com.zealotpz.quartz.entity.Result;
import com.zealotpz.quartz.enums.CommonEnum;
import com.zealotpz.quartz.enums.JobStatus;
import com.zealotpz.quartz.expection.MyExpection;
import com.zealotpz.quartz.mapper.JobMapper;
import com.zealotpz.quartz.model.QuartzJob;
import com.zealotpz.quartz.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.EverythingMatcher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class JobServiceImpl implements JobService {

    private static final String TRIGGER_IDENTITY = "trigger";

    @Autowired
    private Scheduler scheduler;
    @Resource
    private JobMapper jobMapper;
    @Autowired
    private SimpleTriggerListener simpleTriggerListener;

    @Override
    public PageInfo<QuartzJob> listQuartzJob(String jobName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<QuartzJob> jobList = jobMapper.listJob(jobName);
        return new PageInfo(jobList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveJob(QuartzJob quartz) {
        try {
            if (scheduler.checkExists(JobKey.jobKey(quartz.getJobName(), quartz.getJobGroup()))) {
                log.error("Job已经存在, jobName={},jobGroup={}", quartz.getJobName(), quartz.getJobGroup());
                throw new MyExpection(String.format("Job已经存在, jobName=%s,jobGroup=%s", quartz.getJobName(), quartz.getJobGroup()));
            }

            schedulerJob(quartz);
            quartz.setTriggerState(JobStatus.RUNNING.getStatus());
            quartz.setOldJobGroup(quartz.getJobGroup());
            quartz.setOldJobName(quartz.getJobName());
            quartz.setGmtCreate(LocalDateTime.now());

            jobMapper.saveJob(quartz);
        } catch (Exception e) {
            log.error("添加job失败, quartz" + e.getMessage(), e);
            return Result.failure();
//            throw new MyExpection("类名不存在或执行表达式错误");
        }
        return Result.ok();
    }

    @Override
    public Result<String> triggerJob(String jobName, String jobGroup) {
        try {
            if (scheduler.checkExists(JobKey.jobKey(jobName, jobGroup))) {
                JobKey key = new JobKey(jobName, jobGroup);
                //增加执行触发器 SimpleTrigger
                simpleTriggerListener.setName("SimpleTrigger");
                scheduler.getListenerManager().addTriggerListener(simpleTriggerListener, EverythingMatcher.allTriggers());
                //触发任务
                scheduler.triggerJob(key);
            } else {
                log.info("任务信息有误:[{}]-[{}]", jobGroup, jobName);
            }
        } catch (SchedulerException e) {
            log.error("定时任务" + e.getMessage(), e);
            return Result.failure();
        }
        return Result.ok();
    }

    @Override
    public Result<String> getTriggerState(String jobName, String jobGroup) {
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_IDENTITY + jobName, jobGroup);
        String name = null;
        try {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            name = triggerState.name();
        } catch (SchedulerException e) {
            log.error("定时任务" + e.getMessage(), e);
        }
        return Result.ok(name);
    }

    @Override
    public Result<String> triggerJobOnce(String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            log.error("定时任务" + e.getMessage(), e);
        }
        return Result.ok();
    }

    @Override
    public Result<String> pauseJob(String jobName, String jobGroup) {
        try {
            JobKey key = new JobKey(jobName, jobGroup);
            if (scheduler.checkExists(JobKey.jobKey(jobName, jobGroup))) {
                scheduler.pauseJob(key);
                jobMapper.updateJobStatus(jobName, jobGroup, JobStatus.PAUSED.getStatus());
            }
        } catch (SchedulerException e) {
            log.error("定时任务" + e.getMessage(), e);
        }
        return Result.ok();
    }

    @Override
    public Result<String> resumeJob(String jobName, String jobGroup) {

        try {
            if (scheduler.checkExists(JobKey.jobKey(jobName, jobGroup))) {
                JobKey key = new JobKey(jobName, jobGroup);
                scheduler.resumeJob(key);
                jobMapper.updateJobStatus(jobName, jobGroup, JobStatus.RUNNING.getStatus());
            }
        } catch (SchedulerException e) {
            log.error("定时任务" + e.getMessage(), e);
            return Result.failure();
        }
        return Result.ok();
    }

    @Override
    public Result<String> modifyJobTime(String jobName, String jobGroup, String cronExpression) {
        TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_IDENTITY + jobName, jobGroup);
        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) {
                log.info("修改时间-查询触发器有误 , jobName={},jobGroup={},cronExpress={}", jobName, jobGroup, cronExpression);
                return new Result("00000001", "查询触发器有误");
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cronExpression)) {
                // trigger已存在，则更新相应的定时设置

                //withMisfireHandlingInstructionIgnoreMisfires（所有misfire的任务会马上执行,打个比方，如果9点misfire了，在10：15系统恢复之后，9点，10点的misfire会马上执行
                //withMisfireHandlingInstructionDoNothing(所有的misfire不管，执行下一个周期的任务)
                //withMisfireHandlingInstructionFireAndProceed（会合并部分的misfire,正常执行下一个周期的任务）
                //1，不触发立即执行;2，等待下次Cron触发频率到达时刻开始按照Cron频率依次执行
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
//                scheduler.resumeTrigger(trigger.getKey());
                scheduler.rescheduleJob(trigger.getKey(), trigger);

                //更新 myJob信息
                Result<QuartzJob> jobResult = getJob(jobName, jobGroup);
                if (!jobResult.isSuccess() || null == jobResult.getData()) {
                    log.info("更新定时任务[{}]时间-myJob 信息修改有误", jobName);
                    return Result.failure();
                }
                QuartzJob quartzJob = jobResult.getData();
                quartzJob.setCronExpression(cronExpression);
                jobMapper.updateJob(quartzJob);
            }
        } catch (Exception e) {
            log.error("修改modifyJobTime 失败, jobName={},jobGroup={},cronExpress={}", jobName, jobGroup, cronExpression);
            throw new MyExpection("类名不存在或执行表达式错误");
        }
        return Result.ok();
    }

    @Override
    public Result<String> removeJob(String jobName, String jobGroup) {
        try {
            if (scheduler.checkExists(JobKey.jobKey(jobName, jobGroup))) {
                TriggerKey triggerKey = TriggerKey.triggerKey(TRIGGER_IDENTITY + jobName, jobGroup);
                scheduler.pauseTrigger(triggerKey);                                 // 停止触发器
                scheduler.unscheduleJob(triggerKey);                                // 移除触发器
                scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));              // 删除任务
                jobMapper.removeQuartzJob(jobName, jobGroup);
            }
        } catch (Exception e) {
            log.error("定时任务" + e.getMessage(), e);
            return Result.failure();
        }
        return Result.ok();
    }

    @Override
    public Result<QuartzJob> getJob(String jobName, String jobGroup) {
        QuartzJob job = jobMapper.getJob(jobName, jobGroup);
        if (null != job) {
            return Result.ok(job);
        }
        return Result.failure();
    }

    @Override
    public Result<String> updateJob(QuartzJobDTO quartzJobDTO) {
        try {
            scheduler.deleteJob(new JobKey(quartzJobDTO.getOldJobName(), quartzJobDTO.getOldJobGroup()));
//            QuartzJob quartzJob = new QuartzJob();
//            ObjectUtils.copyObjectField(quartzJobDTO, quartzJob);
//            schedulerJob(quartzJob);

            Result<QuartzJob> jobResult = getJob(quartzJobDTO.getOldJobName(), quartzJobDTO.getOldJobGroup());
            if (!jobResult.isSuccess() || null == jobResult.getData()) {
                log.info("更新定时任务[{}]-[{}]信息修改有误", quartzJobDTO.getOldJobName(), quartzJobDTO.getOldJobGroup());
                return Result.ok();
            }
            //查询 myJob 任务信息
            QuartzJob quartzJob = jobResult.getData();
            BeanUtils.copyProperties(quartzJobDTO, quartzJob, getNullPropertyNames(quartzJobDTO));
            schedulerJob(quartzJob);

            quartzJob.setOldJobGroup(quartzJobDTO.getJobGroup());
            quartzJob.setOldJobName(quartzJobDTO.getJobName());
            jobMapper.updateJob(quartzJob);
        } catch (Exception e) {
            log.error("定时任务" + e.getMessage(), e);
            return Result.failure();
        }
        return Result.ok();
    }

    @Override
    public Result<String> updateJobBase(QuartzJob quartzJob) {
        Result<QuartzJob> jobResult = getJob(quartzJob.getJobName(), quartzJob.getJobGroup());
        if (!jobResult.isSuccess() || null == jobResult.getData()) {
            log.info("更新定时任务[{}]-[{}]信息修改有误", quartzJob.getJobName(), quartzJob.getJobGroup());
            return Result.ok(CommonEnum.SET_EXAMPLE_CRITERIA);
        }
        //查询 myJob 任务信息
        QuartzJob oldQuartzJob = jobResult.getData();
        quartzJob.setId(oldQuartzJob.getId());
        jobMapper.updateJobBase(quartzJob);
        return Result.ok();
    }

    @Override
    public void schedulerJob(QuartzJob job) {
        try {
            //构建job信息
            Class cls = Class.forName(job.getJobClassName());
//            cls.newInstance(); // 检验类是否存在
            JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(job.getJobName(), job.getJobGroup())
                    .withDescription(job.getDescription()).build();
            // 触发时间点
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression().trim());
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(TRIGGER_IDENTITY + job.getJobName(), job.getJobGroup())
                    .startNow().withSchedule(cronScheduleBuilder).build();
            //增加执行触发器 SimpleTrigger
            simpleTriggerListener.setName("SimpleTrigger");
            scheduler.getListenerManager().addTriggerListener(simpleTriggerListener, EverythingMatcher.allTriggers());
            //交由Scheduler安排触发
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error("定时任务" + e.getMessage(), e);
            log.error("新增job失败:[{}]", job.getJobName());
            throw new MyExpection("新增job失败");
        }
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

}
