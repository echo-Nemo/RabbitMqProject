package com.echo;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testCallBack() {
        // 定义一个回调函数
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
           /*
           correlationData: 相关的配置信息
           b: 消息是否从 生产者发送到exchange
           s: 失败的原因
            */
                System.out.println("confirm 方法被执行啦");
                if (b) {
                    System.out.println("消息发送成功" + s);
                } else {
                    System.out.println("消息发送失败" + s);
                }
            }
        });

        // 生产者进行消息的发
        rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "i am confirmcallback");
    }

    // 回退模式 从exchange-->queue  只有投递失败才会被执行
    @Test
    public void testConfirmCReturnBack() {
        /*
        returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey)
         */
        // 设置交换机处理失败消息的模式
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println(" i am fialure, i am returncallback");
                System.out.println(message);
                System.out.println(i);
                System.out.println(s);
                System.out.println(s1);
                System.out.println(s2);
            }
        });

        rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "i am confirmcallback");
    }

    // 只发送消息
    @Test
    public void sendMessage() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend("test_exchange_confirm", "confirm", "i am message" + i);
        }
    }

    // 队列或消息的过期时间
    @Test
    public void testTTL() {

        // 1.设置队列过期  对应的过期时间在配置文件中有配置
        // rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hello", "我是队列过期时间啊");

        // 2.设置 队列中某条消息单独的过期时间
        /*
          public void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException {
        this.convertAndSend(exchange, routingKey, message, messagePostProcessor, (CorrelationData)null);
    }
         */
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置消息的信息
                message.getMessageProperties().setExpiration("5000"); // 设置 消息的过期时间为5s
                return message;
            }
        };

        // 单独设置 队列中的某条消息的过期时间
        // rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hello", "我是过期的消息", messagePostProcessor);

        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hello", "我是过期的消息", messagePostProcessor);
            } else {
                rabbitTemplate.convertAndSend("test_exchange_ttl", "ttl.hello", "我是正常的消息");
            }
        }

    }

    // 测试死信队列
    @Test
    public void testDlx() {
        /*
        消息怎么成为死信消息
         */
        // 1.消息在过期时间内未被消费者消费,成为死信消息，被存在死信队列中
        // rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.hello", "我是过期时间为10s的消息");

        // 2.消息的条数超过了队列中消息的最大的条数
//        for (int i = 0; i < 10; i++) {
//            rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.hello", "我是消息" + i);
//        }
        // 3.消费者拒绝接受消息
        rabbitTemplate.convertAndSend("test_exchange_dlx", "test.dlx.hello", "我是一条孤单的消息，你会拒绝我吗");
    }

    /*
    测试延迟队列(rabbitmq中没有延迟队列 可用 死信队列+ttl(延期时间)来代替)
     */
    @Test
    public void testDelayQueue() throws Exception {
        rabbitTemplate.convertAndSend("order_exchange", "order.delay", "我是一个为付款的订单咯，锁定的时间为10s");
        for (int i = 10; i > 0; i--) {
            System.out.println(i);
            Thread.sleep(1000);
        }
    }

    // 测试消息的可靠性传输
    /*
    producer->exchange 有一个confirmCallback
     */
    @Test
    public void testConfirm() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("confim方法被执行了");
                if (b) {
                    System.out.println("消息发送成功" + s);
                } else {
                    System.out.println("消息发送失败" + s);
                }
            }
        });


        // 进行消息的发送 发送成功时 会执行confirm方法
        rabbitTemplate.convertAndSend("confirm_callback_exchange", "confirm", "我是消息的生产者啦");

    }

    // 消息的发送 从 producer-->queue  returnCallback的机制  消息失败后进行消息才会执行

    @Test
    public void testReturnCallBack() {
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println("我传输消息失败了啦" + message);
            }
        });

        // 进行消息的发送 发送成功时 会执行confirm方法
        rabbitTemplate.convertAndSend("confirm_callback_exchange", "confirm", "我是消息的生产者啦");

    }


}
