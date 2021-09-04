package com.echo.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class QosLister implements ChannelAwareMessageListener {
    /*
    消费端限流
        1、确保ack确认机制为手动
        2. 配置消费端每次 消费的消息的条数 prefetch="消费的个数", 消费端每次从Mq中拉取prefetch条消息哦，手动确认消费完毕后，才会去拉取下一条消息
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        // 获取消息o
        System.out.println(message.toString());
        // 业务逻辑的处理

        // 进行消息的签收
        long tag = message.getMessageProperties().getDeliveryTag();

        channel.basicAck(tag, true);
    }
}
