package xuecheng.test.rabbite.mq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import xuecheng.test.rabbite.config.RabbitmqConfig;

/**
 * @author hftang
 * @date 2019-03-06 14:05
 * @desc
 */
@Component
public class ReceiveMsg {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void testReceMsg(String msg){
        System.out.println("接收到：msg:::"+msg);
    }
}
