package com.zealotpz.quartz.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * description: 钉钉推送
 * 通过钉钉群机器人实现
 *
 * @author: zealotpz
 * create: 2022-01-07 11:35
 **/

@Component
public class DingTalkUtil {

    /**
     * 钉钉机器人发送地址
     */
    private static final String URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    /**
     * 钉钉机器人 token
     */
    public static String DING_TALK_ACCESS_TOKEN;

    @Value("${ding.talk.access.token:1231231}")
    public void setAccessToken(String accessToken) {
        DING_TALK_ACCESS_TOKEN = accessToken;
    }

    /**
     * 推送钉钉消息的线程池
     */
    private static volatile ExecutorService EXECUTOR_SERVICE;

    /**
     * 推送钉钉消息
     */
    public static void sendMsg(String content, String mobile) {
        //初始化线程池
        if (null == EXECUTOR_SERVICE) {
            synchronized (DingTalkUtil.class) {
                if (null == EXECUTOR_SERVICE) {
                    EXECUTOR_SERVICE = new ThreadPoolExecutor(1, 10, 0L,
                            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
                }
            }
        }

        //发送消息
        EXECUTOR_SERVICE.execute(() -> {
            //设置消息类型
            JSONObject params = new JSONObject();
            params.put("msgtype", "text");
            //设置文本内容
            JSONObject text = new JSONObject();
            text.put("content", content);
            params.put("text", text);
            //设置@人员
            JSONObject at = new JSONObject();
            at.put("atMobiles", "[".concat(mobile).concat("]"));
            params.put("at", at);
            // 发送消息
            HttpUtil.sendJsonPost(URL + DING_TALK_ACCESS_TOKEN, params.toJSONString());
        });
    }

}
