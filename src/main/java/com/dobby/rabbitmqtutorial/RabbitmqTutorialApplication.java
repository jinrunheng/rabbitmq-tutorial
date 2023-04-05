package com.dobby.rabbitmqtutorial;

import com.dobby.rabbitmqtutorial.service.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitmqTutorialApplication implements ApplicationRunner {

    @Autowired
    private Consumer consumer;

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqTutorialApplication.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        consumer.handleMessage();
    }
}
