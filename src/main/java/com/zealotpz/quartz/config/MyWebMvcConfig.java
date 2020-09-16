package com.zealotpz.quartz.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description: SpringBoot的Cors跨域设置
 *
 * @author: zealotpz
 * create: 2020-08-31 15:36
 **/
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //配置允许跨域访问的路径
        registry.addMapping("/job/**") //对应的接口
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .maxAge(3600);
    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //需要配置1：----------- 需要告知系统，这是要被当成静态文件的！
//        //第一个方法设置访问路径前缀，第二个方法设置资源路径
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
//    }
}
