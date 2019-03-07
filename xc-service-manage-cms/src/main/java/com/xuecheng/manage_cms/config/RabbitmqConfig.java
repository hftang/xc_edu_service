package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hftang
 * @date 2019-03-06 15:40
 * @desc
 */

@Configuration
public class RabbitmqConfig {

    //在生产方 只需要配置个 交换机  因为面对无数的队列


    //交换机名称

    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";




    //创建 交换器

    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return  ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }





}
