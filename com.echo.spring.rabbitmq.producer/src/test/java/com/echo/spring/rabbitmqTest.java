package com.echo.spring;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rabbitmq-producer.xml")
public class rabbitmqTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /*
       public void convertAndSend(String routingKey, Object object) throws AmqpException {
        this.convertAndSend(this.exchange, routingKey, object, (CorrelationData)null);
    }
     */
    @Test
    public void testHelloWorld() {
        // 发送消息
        String body="hello queue";
        rabbitTemplate.convertAndSend("spring_queue",body);
    }

    // 使用广播的方式进行消息分发送
    /*
      public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException {
        this.convertAndSend(exchange, routingKey, object, (CorrelationData)null);
    }
     */
    @Test
    public void testFanout() {
        // 发送消息
        String body1="hello fanout queue1";
        String body2="hello fanout queue2";
        rabbitTemplate.convertAndSend("spring_fanout_exchange","spring_fanout_queue_1",body1);
        rabbitTemplate.convertAndSend("spring_fanout_exchange","spring_fanout_queue_2",body1);
    }

    // 使用通配符的方式发送消息
    @Test
    public void testTopic() {
        // 发送消息
        String body1="hello topic queue";
        rabbitTemplate.convertAndSend("spring_topic_exchange","heima.hello.world",body1);
    }

    // 消息的发送





}
