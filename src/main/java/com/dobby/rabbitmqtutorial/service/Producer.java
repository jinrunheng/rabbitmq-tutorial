package com.dobby.rabbitmqtutorial.service;

import com.rabbitmq.client.impl.nio.BlockingQueueNioQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Dooby Kim
 * @Date 2023/3/12 8:45 下午
 * @Version 1.0
 */
@Service
@Slf4j
public class Producer {

    final String QUEUE = "queue.test";
    final String EXCHANGE = "exchange.test";
    final String ROUTING_KEY = "key.test";

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    public void initRabbit() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost("127.0.0.1");
//        connectionFactory.setPort(5672);
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//        // 声明 Exchange
//        Exchange exchange = new DirectExchange(EXCHANGE, false, false);
//        // 声明 Queue
//        Queue queue = new Queue(QUEUE, false);
//        // 声明 Binding
//        Binding binding = new Binding(
//                QUEUE,
//                Binding.DestinationType.QUEUE,
//                EXCHANGE,
//                ROUTING_KEY,
//                null
//        );
//        rabbitAdmin.declareExchange(exchange);
//        rabbitAdmin.declareQueue(queue);
//        rabbitAdmin.declareBinding(binding);
//
//    }

    public void sendMessage() {
        String messageToSend = "test message";
        MessageProperties messageProperties = new MessageProperties();
        //  设置单条消息 TTL 为 1 min
        messageProperties.setExpiration("60000");
        Message message = new Message(messageToSend.getBytes(), messageProperties);
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.send(
                EXCHANGE,
                ROUTING_KEY,
                message,
                correlationData
        );
        log.info("message sent");
    }
}
