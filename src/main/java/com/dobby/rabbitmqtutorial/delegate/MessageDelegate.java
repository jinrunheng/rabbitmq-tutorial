package com.dobby.rabbitmqtutorial.delegate;

import com.dobby.rabbitmqtutorial.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Dooby Kim
 * @Date 2023/3/12 8:39 下午
 * @Version 1.0
 */
@Slf4j
@Component
public class MessageDelegate {

    public void handleMessage(byte[] msgBody) {
        log.info("invoke handleMessage,msgBody : {}", new String(msgBody));
    }

    //    public void handle(byte[] msgBody) {
//        log.info("invoke handle,msgBody : {}", new String(msgBody));
//    }
    public void handle(Order order) {
        log.info("invoke handle, msg : {}", order);
    }
}
