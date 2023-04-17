package com.dobby.rabbitmqtutorial.service;

import com.dobby.rabbitmqtutorial.entity.Order;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author Dooby Kim
 * @Date 2023/4/5 10:23 下午
 * @Version 1.0
 */
@Service
@Slf4j
@RabbitListener(
        containerFactory = "rabbitListenerContainerFactory",
        queues = "queue.test"
)
public class Consumer {

    final String QUEUE = "queue.test";

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @RabbitListener(
//            containerFactory = "rabbitListenerContainerFactory",
//            queues = "queue.test"
//    )
    @RabbitHandler(isDefault = true)
    public void handle(Order order) {
        log.info("invoke handle, msg : {}", order);
    }

    // @Async
    public void handleMessage() {
        try (
                Connection connection = rabbitTemplate.getConnectionFactory().createConnection();
                Channel channel = connection.createChannel(false);
        ) {
            channel.basicConsume(QUEUE,
                    true,
                    deliverCallback,
                    consumerTag -> {
                    }
            );
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("error message : {}", e.getMessage());
        }
    }

    DeliverCallback deliverCallback = (this::handle);

    private void handle(String consumerTag, Delivery message) {
        String messageBody = new String(message.getBody());

        log.info("receive message");
        log.info("consumerTag : {}", consumerTag);
        log.info("messageBody : {}", messageBody);

    }
}
