package xuecheng.test.rabbite.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hftang
 * @date 2019-03-06 13:14
 * @desc
 */
@Configuration
public class RabbitmqConfig {

    public static final String QUEUE_INFORM_EMAIL="queue_inform_email";
    public static final String QUEUE_INFORM_SMS="queue_inform_sms";
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String ROUTINGKEY_EMAIL="inform.#.email.#";
    public static final String ROUTINGKEY_SMS="inform.#.sms.#";

    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //持久化的交换机
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }


    //声明队列

    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }

    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }


    //绑定交换机和队列

    //名称注入 交换机 队列

    @Bean
    public Binding BINDING_ROUTINGKEY_EMAIL(@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange,
                                            @Qualifier(QUEUE_INFORM_EMAIL) Queue queue){

        //
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();

    }


    @Bean
    public Binding  BINDING_ROUTINGKEY_SMS(@Qualifier(QUEUE_INFORM_SMS) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }
}
