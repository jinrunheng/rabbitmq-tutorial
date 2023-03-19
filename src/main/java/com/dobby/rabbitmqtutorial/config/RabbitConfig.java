package com.dobby.rabbitmqtutorial.config;

import com.dobby.rabbitmqtutorial.delegate.MessageDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author Dooby Kim
 * @Date 2023/3/18 4:26 下午
 * @Version 1.0
 */
@Configuration
@Slf4j
public class RabbitConfig {

    final String QUEUE = "queue.test";
    final String EXCHANGE = "exchange.test";
    final String ROUTING_KEY = "key.test";

    @Resource
    private MessageDelegate messageDelegate;

//    /**
//     * 声明队列 queue.test
//     *
//     * @return
//     */
//    @Bean
//    public Queue testQueue() {
//        return new Queue(QUEUE);
//    }
//
//
//    /**
//     * 声明交换机 exchange.test
//     *
//     * @return
//     */
//    @Bean
//    public Exchange testExchange() {
//        return new DirectExchange(EXCHANGE);
//    }
//
//    /**
//     * 声明绑定关系
//     *
//     * @return
//     */
//    @Bean
//    public Binding testBinding() {
//        return new Binding(QUEUE,
//                Binding.DestinationType.QUEUE,
//                EXCHANGE,
//                ROUTING_KEY,
//                null);
//    }


//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost("localhost");
//        connectionFactory.setPort(5672);
//        connectionFactory.setUsername("guest");
//        connectionFactory.setPassword("guest");
//        connectionFactory.createConnection();
//        return connectionFactory;
//    }
//
//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//        rabbitAdmin.setAutoStartup(true);
//        return rabbitAdmin;
//    }

//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        // 开启消息返回机制
//        rabbitTemplate.setMandatory(true);
//        rabbitTemplate.setReturnsCallback(returned -> {
//            log.info("message:{}", returned.getMessage().toString());
//            log.info("replyCode:{}", returned.getReplyCode());
//            log.info("replyText:{}", returned.getReplyText());
//            log.info("exchange:{}", returned.getExchange());
//            log.info("routingKey:{}", returned.getRoutingKey());
//        });
//        // 开启消息确认机制
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            log.info("correlationData:{}", correlationData);
//            log.info("ack:{}", ack);
//            log.info("cause:{}", cause);
//        });
//        return rabbitTemplate;
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
//        messageListenerContainer.setQueueNames(QUEUE);
//        messageListenerContainer.setConcurrentConsumers(1);
//        messageListenerContainer.setMaxConcurrentConsumers(5);
//        // 消费端开启手动确认
//        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        // 消费端限流
//        messageListenerContainer.setPrefetchCount(20);
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
//        // 设置代理
//        messageListenerAdapter.setDelegate(messageDelegate);
//        messageListenerContainer.setMessageListener(messageListenerAdapter);
//        return messageListenerContainer;
//    }

}