package com.dobby.rabbitmqtutorial.controller;

import com.dobby.rabbitmqtutorial.service.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Dooby Kim
 * @Date 2023/3/12 8:50 下午
 * @Version 1.0
 */
@RestController
public class TestController {

    @Autowired
    private Producer producer;

    @GetMapping("/test")
    public String send() {
        producer.sendMessage();
        // producer.initRabbit();
        return "success";
    }
}
