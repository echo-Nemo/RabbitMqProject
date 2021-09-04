package com.echo.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1.接收转换消息
            System.out.println(new String(message.getBody()));

            //2. 处理业务逻辑
            System.out.println("处理业务逻辑...");
            System.out.println("我是库存系统，判断10s后该用户是否以付款");
            //3. 手动签收
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage().toString());
            //4.拒绝签收
            /*
            第三个参数：requeue：重回队列。设置为false后，消息进入到死信队列中
             */
            channel.basicNack(deliveryTag, true, false);
           // channel.basicReject(deliveryTag,false);
        }
    }
}
