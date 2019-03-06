package com.xuecheng.test.rabbite;

import com.xuecheng.test.rabbite.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hftang
 * @date 2019-03-06 13:47
 * @desc
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSpringBootRabbitmq {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Test
    public void testSendEmail(){
        String msg="sender send to you message !!!";
        /**
         * 交换机
         * routingkey
         * 消息内容
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",msg);
    }

}
