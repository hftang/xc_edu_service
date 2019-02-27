package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hftang
 * @date 2019-02-27 10:41
 * @desc 启动类
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms") //实体类扫描
@ComponentScan(basePackages = "com.xuecheng.api") //接口扫描
@ComponentScan(basePackages = "com.xuecheng.manage_cms")//扫描本项目下所有的实体类
public class ManageCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }
}
