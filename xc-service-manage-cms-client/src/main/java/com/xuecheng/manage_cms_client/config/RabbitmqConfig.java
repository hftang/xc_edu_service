package com.xuecheng.manage_cms_client.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hftang
 * @date 2019-03-06 15:40
 * @desc
 */

@Configuration
public class RabbitmqConfig {

    //队列bean名称

    public static final String QUEUE_CMS_POSTPAGE="queue_cms_postpage";

    //交换机名称

    public static final String EX_ROUTING_CMS_POSTPAGE="ex_routing_cms_postpage";

    //队列的名称
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage_name;

    //routingkey 及站点
    @Value("${xuecheng.mq.routingKey}")
    public  String routingKey;

    //创建 交换器

    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EX_ROUTING_CMS_POSTPAGE(){
        return  ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE(){
        return new Queue(queue_cms_postpage_name);
    }

    //绑定队列 和 交换机

    @Bean
    public Binding Binding_QUEUE_INFORM_SMS(@Qualifier(QUEUE_CMS_POSTPAGE) Queue queue,
                                            @Qualifier(EX_ROUTING_CMS_POSTPAGE) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }



}
