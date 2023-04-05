package com.dobby.rabbitmqtutorial.config;

import com.dobby.rabbitmqtutorial.delegate.MessageDelegate;
import com.dobby.rabbitmqtutorial.service.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 声明队列 queue.test
     *
     * @return
     */
    @Bean
    public Queue testQueue() {
        return new Queue(QUEUE);
    }


    /**
     * 声明交换机 exchange.test
     *
     * @return
     */
    @Bean
    public Exchange testExchange() {
        return new DirectExchange(EXCHANGE);
    }

    /**
     * 声明绑定关系
     *
     * @return
     */
    @Bean
    public Binding testBinding() {
        return new Binding(QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                ROUTING_KEY,
                null);
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        // 开启消息确认
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        // 开启消息返回
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 开启消息返回机制
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnsCallback(returned -> {
            // 说明消息不可达
            log.info("message:{}", returned.getMessage().toString());
            log.info("replyCode:{}", returned.getReplyCode());
            log.info("replyText:{}", returned.getReplyText());
            log.info("exchange:{}", returned.getExchange());
            log.info("routingKey:{}", returned.getRoutingKey());
        });
        // 开启消息确认机制
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("send msg to Broker success");
                log.info("correlationData : {}", correlationData);
            } else {
                log.info("send msg to Broker fail");
                log.info("cause : {}", cause);
            }
        });
        return rabbitTemplate;
    }

//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
//        SimpleMessageListenerContainer messageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
//        // 设置监听队列
//        messageListenerContainer.setQueueNames(QUEUE);
//        // 设置消费者线程数量
//        messageListenerContainer.setConcurrentConsumers(3);
//        // 设置最大的消费者线程数量
//        messageListenerContainer.setMaxConcurrentConsumers(5);
//        // 消费端开启手动确认
//        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//
////        messageListenerContainer.setMessageListener(new MessageListener() {
////            @Override
////            public void onMessage(Message message) {
////                log.info("receive message:{}", message);
////            }
////        });
//        messageListenerContainer.setMessageListener(new ChannelAwareMessageListener() {
//
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                log.info("receive message:{}", message);
//                // 消费端手动确认
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//            }
//        });
//
//        // 消费端限流
//        messageListenerContainer.setPrefetchCount(20);
//        // MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter();
//        // 设置代理
//        // messageListenerAdapter.setDelegate(messageDelegate);
//        // messageListenerContainer.setMessageListener(messageListenerAdapter);
//        return messageListenerContainer;
//    }

}