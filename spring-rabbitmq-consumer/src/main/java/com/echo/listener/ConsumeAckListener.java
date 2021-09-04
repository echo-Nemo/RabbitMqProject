package com.echo.listener;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumeAckListener implements ChannelAwareMessageListener {
    /*
     public void basicAck(long deliveryTag, boolean multiple) throws IOException {
        this.delegate.basicAck(deliveryTag, multiple);
    }
    multiple:是否对消息进行批量的确认
     */

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 接受从生产端发送的消息

        try {
            System.out.println("我是生产者端的消息" + message);
            // 处理业务逻辑
            int i = 3 / 0;
            System.out.println("我在处理业务逻辑");
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            // 业务逻辑发生异常，生产者重新对消息进行发送  第3个参数 是将消息重新回到队列中
            channel.basicNack(deliveryTag, true, true);
        }


    }
}
