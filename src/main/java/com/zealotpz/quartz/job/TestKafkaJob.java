package com.zealotpz.quartz.job;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * description: 测试定时任务用-仅打印相关信息
 * 消息队列采用 kafka
 *
 * @author zealotpz*/

@Slf4j
@Component
public class TestKafkaJob extends QuartzJobBean {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) {

        /*获取job的属性*/
        JobKey jobkey = context.getJobDetail().getKey();

        /*获取Trigger的属性*/
        TriggerKey tKey = context.getTrigger().getKey();

        log.info("定时任务参数:[{}]-任务组:[{}]-触发器:[{}]--------->时间:{}", jobkey.getName(), jobkey.getGroup(), tKey.getName(), new Date());

        JSONObject jsonObject = new JSONObject();
        kafkaTemplate.send("test-topic","测试测试");

    }

}
