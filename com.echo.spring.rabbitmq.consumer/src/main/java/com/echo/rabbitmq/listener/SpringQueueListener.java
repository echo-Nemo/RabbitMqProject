package com.echo.rabbitmq.listener;

        import org.springframework.amqp.core.Message;
        import org.springframework.amqp.core.MessageListener;

// 一个监听的类，实现MessageListener
public class SpringQueueListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        // 消息的打印
        System.out.println(new String(message.getBody()));
    }
}
