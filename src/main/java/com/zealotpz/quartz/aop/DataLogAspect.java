package com.zealotpz.quartz.aop;

import com.alibaba.fastjson.JSONObject;
import com.zealotpz.quartz.annotation.DataOperationLog;
import com.zealotpz.quartz.utils.DingTalkUtil;
import com.zealotpz.quartz.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * description: 数据操作 aop
 * 获取入库数据发送至 mq
 *
 * @author: zealotpz
 * create: 2022-01-06 16:51
 **/

@Slf4j
@Aspect
@Component
public class DataLogAspect {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Around("@annotation(com.zealotpz.quartz.annotation.DataOperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 参数名
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        // 参数值
        Object[] arguments = joinPoint.getArgs();


        Method method = getMethod(joinPoint);

        // 获取注解参数
        DataOperationLog annotation = method.getAnnotation(DataOperationLog.class);
        // 消息体
        String msg = annotation.msg();
        // 业务类型
        String bizType = annotation.bizType();

        // 消息通知类型
        String notifyType = annotation.notifyType();
        if ("1".equals(notifyType)) {
            //==> rabbitmq 通知
            // 队列信息
            String mqQueue = annotation.mqQueue();
            // 发送消息至 mq
            kafkaTemplate.send(mqQueue, msg);
        } else if ("2".equals(notifyType)) {
            // ==> 钉钉推送

            DingTalkUtil.sendMsg(msg, "");
        } else if ("3".equals(notifyType)) {
            // ==> 微信 pushplus 推送
            JSONObject params = new JSONObject();
            // token
            params.put("token", "");
            params.put("title", "测试");
            params.put("content", msg);
            params.put("template", "markdown");
//            params.put("template", "cloudMonitor");

            // 微信推送
            String postResult = HttpUtil.sendJsonPost("http://www.pushplus.plus/send", params.toJSONString());
            System.out.println("------->" + postResult);
        }


//        kafkaTemplate.send("test-topic", "测试测试");
        return joinPoint.proceed();
    }

    protected Method getMethod(JoinPoint joinPoint) {
        Method method = null;
        try {
            Signature signature = joinPoint.getSignature();
            MethodSignature ms = (MethodSignature) signature;
            Object target = joinPoint.getTarget();
            method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
        } catch (NoSuchMethodException e) {
            log.error("SystemLogAspect getMethod error", e);
        }
        return method;
    }
}
