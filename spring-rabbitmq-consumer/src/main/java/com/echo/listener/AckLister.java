package com.echo.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class AckLister implements ChannelAwareMessageListener {

    /*
    public void basicAck(long deliveryTag, boolean multiple) throws IOException {
        this.delegate.basicAck(deliveryTag, multiple);
    }

     */

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            //1.接收转换消息
            System.out.println(new String(message.getBody()));

            //2. 处理业务逻辑
            System.out.println("处理业务逻辑...");
            int i = 3 / 0;//出现错误
            //3. 手动签收
            System.out.println("接受消息成功啦");
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("接受消息失败啦");
            System.out.println(e.getMessage().toString());
            //4.拒绝签收
            /*
            第三个参数：requeue：重回队列。如果设置为true，则消息重新回到queue，broker会重新发送该消息给消费端
             */
            channel.basicNack(deliveryTag, true, true);
            //channel.basicReject(deliveryTag,true);
        }
    }
}
