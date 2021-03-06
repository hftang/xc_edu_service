package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author hftang
 * @date 2019-02-27 10:41
 * @desc 启动类
 */

@EnableDiscoveryClient
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.cms") //实体类扫描
@ComponentScan(basePackages = "com.xuecheng.api") //接口扫描
@ComponentScan(basePackages = "com.xuecheng.framework") //扫描一下异常类 controllerAdvice 这个类
@ComponentScan(basePackages = "com.xuecheng.manage_cms.*")//扫描本项目下所有的实体类

@EnableMongoRepositories(basePackages = {"com.xuecheng.manage_cms.dao"})
public class ManageCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }

    //配置一个bean rest

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
