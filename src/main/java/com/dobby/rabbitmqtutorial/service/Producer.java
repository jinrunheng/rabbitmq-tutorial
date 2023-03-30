package com.dobby.rabbitmqtutorial.service;

import com.dobby.rabbitmqtutorial.entity.Order;
import com.dobby.rabbitmqtutorial.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.convertAndSend(
                EXCHANGE,
                ROUTING_KEY,
                messageToSend,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //  设置单条消息 TTL 为 1 min
                        MessageProperties messageProperties = message.getMessageProperties();
                        messageProperties.setExpiration("60000");
                        return message;
                    }
                },
                correlationData
        );
        log.info("message sent");
    }
}
