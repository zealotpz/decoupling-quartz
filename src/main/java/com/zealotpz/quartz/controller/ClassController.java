package com.zealotpz.quartz.controller;

import com.zealotpz.quartz.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * description:
 *
 * @author: zealotpz
 * create: 2020-09-17 16:05
 **/

@Slf4j
@RestController
@RequestMapping("/class")
public class ClassController {

    @GetMapping("/package/getName")
    public Set<String> resume(@RequestParam(value = "packageName", required = false) String packageName) {
        log.info("开始查询包[{}]下全部类名", packageName);
        if (StringUtils.isEmpty(packageName)) {
            packageName = "com.zealotpz.quartz.job";
        }
        return ClassUtil.getClassName(packageName, false);
    }
}
