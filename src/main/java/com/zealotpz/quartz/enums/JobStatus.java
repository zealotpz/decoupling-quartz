package com.zealotpz.quartz.enums;

import lombok.Getter;

@Getter
public enum JobStatus {

    //    Triggers表状态	描述
//    WAITING	创建任务触发器默认状态
//    ACQUIRED	当到达触发时间时，获得状态
//    EXECUTING	运行中，firedTrigger表中
//    COMPLETE	完成状态，任务结束
//    BLOCKED	阻塞状态
//    ERROR	错误状态
//    PAUSED	暂停状态
//    PAUSED_BLOCKED	暂停阻塞状态，非并发下
//    DELETED	删除状态

    PAUSED("PAUSED", "任务暂停"),
    RUNNING("RUNNING", "运行中"),
    COMPLETE("COMPLETE", "成功");


    private String status;
    private String msg;

    JobStatus(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
