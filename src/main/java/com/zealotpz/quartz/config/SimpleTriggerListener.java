package com.zealotpz.quartz.config;

import com.zealotpz.quartz.model.JobTriggerLog;
import com.zealotpz.quartz.service.JobTriggerLogService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * description:
 *
 * @author: zealotpz
 * create: 2020-09-07 18:03
 **/

@Slf4j
@NoArgsConstructor
@Component
public class SimpleTriggerListener implements TriggerListener {

    @Autowired
    private JobTriggerLogService jobTriggerLogService;

    private String name;

    public SimpleTriggerListener(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * (1) Trigger被激发 它关联的job即将被运行
     */
    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        String triggerName = trigger.getKey().getName();
        log.info("{} ----> was fired", triggerName);
    }

    /**
     * (2) Trigger被激发 它关联的job即将被运行,先执行(1)，再执行(2) 如果返回TRUE 那么任务job会被终止
     */
    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        String triggerName = trigger.getKey().getName();
        log.info("{} ----> was not vetoed", triggerName);
        return false;
    }

    /**
     * (3) 当Trigger错过被激发时执行,比如当前时间有很多触发器都需要执行，但是线程池中的有效线程都在工作，那么有的触发器就有可能超时，
     * 错过这一轮的触发。
     */
    @Override
    public void triggerMisfired(Trigger trigger) {
        String triggerName = trigger.getKey().getName();
        log.info("{} ----> was  misfired", triggerName);
    }

    /**
     * (4) 任务完成时触发
     */
    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        TriggerKey triggerKey = trigger.getKey();
        String triggerName = triggerKey.getName();
        log.info("{}-{} ----> is complete", trigger.getJobKey().getName(), triggerName);

        JobTriggerLog build = JobTriggerLog.builder()
                .jobName(trigger.getJobKey().getName()).description(context.getJobDetail().getDescription())
                .triggerName(triggerName).triggerState(triggerInstructionCode.name())
                .gmtCreate(LocalDateTime.now().withNano(0))
                .nextFireTime(context.getTrigger().getNextFireTime()).build();

        log.info(">>>> " + build.toString());
        jobTriggerLogService.addJobTriggerLog(build);
    }


}
