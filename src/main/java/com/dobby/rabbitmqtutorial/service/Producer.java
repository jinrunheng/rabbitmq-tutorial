package com.dobby.rabbitmqtutorial.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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
    private RabbitTemplate rabbitTemplate;

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
