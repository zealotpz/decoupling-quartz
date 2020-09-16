package com.zealotpz.quartz;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zealotpz
 */
@Slf4j
@MapperScan("com.zealotpz.quartz.mapper")
@SpringBootApplication
public class DecouplingQuartzApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DecouplingQuartzApplication.class).web(WebApplicationType.SERVLET).run(args);
        log.info("decoupling-quartz-application startup!");
    }

}
