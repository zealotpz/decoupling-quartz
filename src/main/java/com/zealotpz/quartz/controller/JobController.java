package com.zealotpz.quartz.controller;

import com.github.pagehelper.PageInfo;
import com.zealotpz.quartz.dto.QuartzJobDTO;
import com.zealotpz.quartz.entity.Result;
import com.zealotpz.quartz.model.QuartzJob;
import com.zealotpz.quartz.service.JobService;
import com.zealotpz.quartz.service.TestService;
import com.zealotpz.quartz.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * description:
 * author: zealotpz
 * create: 2019-04-24 15:33
 **/

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private TestService testService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @PostMapping("/add")
    public Result save(@RequestBody QuartzJob quartz) {
        log.info("新增quartz任务:{}", quartz.toString());
        return jobService.saveJob(quartz);
    }

    //    @CrossOrigin
    @PostMapping(value = "/list")
    public PageInfo<QuartzJob> list(@RequestBody QuartzJobDTO quartzJobDTO) {
//        String jobName, Integer pageNum, Integer pageSize
        log.info("查询任务列表:{}", quartzJobDTO.toString());
        return jobService.listQuartzJob(quartzJobDTO.getJobName(), quartzJobDTO.getPageNum(), quartzJobDTO.getPageSize());
    }

    @PostMapping(value = "/getJob")
    public Result<QuartzJob> getJob(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("获取任务信息:{}", quartzJobDTO.toString());
        return jobService.getJob(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup());
    }

    @PostMapping("/trigger")
    public Result<String> trigger(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("触发任务:{}", quartzJobDTO.toString());
        return jobService.triggerJob(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup());
    }

    @PostMapping("/pause")
    public Result<String> pause(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("停止任务:{}", quartzJobDTO.toString());
        return jobService.pauseJob(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup());
    }

    @PostMapping("/resume")
    public Result<String> resume(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("恢复任务:{}", quartzJobDTO.toString());
        return jobService.resumeJob(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup());
    }

    @PostMapping("/remove")
    public Result<String> remove(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("移除任务:{}", quartzJobDTO.toString());
        return jobService.removeJob(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup());
    }

    @RequestMapping(value = "/modifyJobTime", method = RequestMethod.POST)
    public Result<String> modifyJobTime(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("修改任务时间:{}", quartzJobDTO.toString());
        return jobService.modifyJobTime(quartzJobDTO.getJobName(), quartzJobDTO.getJobGroup(), quartzJobDTO.getCronExpression());
    }

    @PostMapping("/updateJob")
    public Result<String> updateJob(@RequestBody QuartzJobDTO quartzJobDTO) {
        log.info("修改任务:{}", quartzJobDTO.toString());
        return jobService.updateJob(quartzJobDTO);
    }

    // 仅修改 描述,起止时间）
    @PostMapping("/updateJobBase")
    public Result<String> updateJobBase(@RequestBody QuartzJob quartzJob) {
        log.info("修改任务基础信息:{}", quartzJob.toString());
        return jobService.updateJobBase(quartzJob);
    }

    /**
     * 测试雪花(16 位)
     * 6312609171247104
      */
    @PostMapping("/test")
    public long test() {

        return SnowFlakeUtil.nextId();
    }

    @PostMapping("/testLog")
    public void testLog() {
        testService.dataOperationLog("11111");
    }


}
