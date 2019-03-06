package com.xuecheng;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author hftang
 * @date 2019-03-06 9:28
 * @desc
 */
public class Consumer {
    private static final  String  QUEUE="helloword";
    private static Connection connection;
    private static Channel channel;

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            //声明队列
            channel.queueDeclare(QUEUE,true,false,false,null);
            //定义消费者
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);


                                /**
                                  * 消费者接收消息调用此方法
                                  * @param consumerTag 消费者的标签，在channel.basicConsume()去指定
                                  * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志
                                        (收到消息失败后是否需要重新发送)
                                  * @param properties
                                  * @param body
                                  * @throws IOException
                                 */


                                //得到交换机
                    String exchange = envelope.getExchange();
                    //路由key
                    String routingKey = envelope.getRoutingKey();

                    //消息id
                    long deliveryTag = envelope.getDeliveryTag();
                    //消息内容
                    String msg = new String(body, "utf-8");

                    System.out.println("接受端接受到的消息：：："+msg);


                }
            };



                    /**
                      * 监听队列String queue, boolean autoAck,Consumer callback
                      * 参数明细
                      * 1、队列名称
                      * 2、是否自动回复，设置为true为表示消息接收到自动向mq回复接收到了，mq接收到回复会删除消息，设置
                        为false则需要手动回复
                      * 3、消费消息的方法，消费者接收到消息后调用此方法
                      */

                    channel.basicConsume(QUEUE,true,defaultConsumer);



        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
