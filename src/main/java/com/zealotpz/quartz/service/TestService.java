package com.zealotpz.quartz.service;

import com.zealotpz.quartz.annotation.DataOperationLog;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zealotpz
 * create: 2022-01-07 15:16
 **/

@Component
public class TestService {

    @DataOperationLog(bizType = "1", msg = "1", notifyType = "3")
    public void dataOperationLog(String msg) {
        System.out.println("------->" + msg);
    }
}
