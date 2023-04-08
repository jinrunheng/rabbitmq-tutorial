package com.dobby.rabbitmqtutorial.component;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * @Author Dooby Kim
 * @Date 2023/4/8 10:53 下午
 * @Version 1.0
 */
@Slf4j
public class MyChannelAwareMessageListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        log.info("receive message:{}", new String(message.getBody()));
        // 消费端手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        // 重回队列
        // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        // 拒绝消息
        // channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }
}
