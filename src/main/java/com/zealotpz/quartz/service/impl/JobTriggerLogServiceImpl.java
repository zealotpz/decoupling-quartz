package com.zealotpz.quartz.service.impl;

import com.zealotpz.quartz.model.JobTriggerLog;
import com.zealotpz.quartz.mapper.JobTriggerLogMapper;
import com.zealotpz.quartz.service.JobTriggerLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description:
 *
 * @author: zealotpz
 * create: 2020-09-08 11:23
 **/

@Slf4j
@Service
public class JobTriggerLogServiceImpl implements JobTriggerLogService {

    @Autowired
    private JobTriggerLogMapper jobTriggerLogMapper;

    @Override
    public void addJobTriggerLog(JobTriggerLog jobTriggerLog) {
        jobTriggerLogMapper.insert(jobTriggerLog);
    }

}


