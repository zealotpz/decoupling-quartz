package com.zealotpz.quartz.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zealotpz.quartz.model.QuartzJob;
import com.zealotpz.quartz.mapper.JobMapper;
import com.zealotpz.quartz.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author: zealotpz
 * create: 2020-08-27 15:50
 **/
@RestController
@RequestMapping("/tl/job")
public class ThJobController {

    @Autowired
    private JobService jobService;
    @Autowired
    private JobMapper jobMapper;

    @GetMapping("/add")
    public String add(@RequestParam(value = "jobName") String jobName,
                      @RequestParam(value = "jobGroup") String jobGroup,
                      @RequestParam(value = "description") String description,
                      @RequestParam(value = "cron") String cron,
                      @RequestParam(value = "className") String className,
                      @RequestParam(value = "jobMessage") String jobMessage
    ) {

        QuartzJob quartz = new QuartzJob();

        quartz.setJobName(jobName); //任务名称
        quartz.setJobGroup(jobGroup); //任务分组
        quartz.setDescription(description); //任务描述
        quartz.setJobClassName(className);
        quartz.setCronExpression(cron); //每天 04:00
        if (!StringUtils.isEmpty(jobMessage)) {
            JSONObject jj = JSON.parseObject(jobMessage);
            quartz.setJobMessage(jj);
        }

        return jobService.saveJob(quartz).getCode();    //  相当于访问 classpath:/templates/jobPage.html
    }


    @GetMapping("/trigger")
    public String trigger(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup") String jobGroup) {
        return jobService.triggerJob(jobName, jobGroup).getCode();
    }

    @GetMapping("/pauseJob")
    public String pauseJob(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup") String jobGroup) {
        return jobService.pauseJob(jobName, jobGroup).getCode();
    }
    @GetMapping("/resumeJob")
    public String resumeJob(@RequestParam(value = "jobName") String jobName, @RequestParam(value = "jobGroup") String jobGroup) {
        return jobService.resumeJob(jobName, jobGroup).getCode();
    }

}
