package com.dobby.rabbitmqtutorial.entity;

import lombok.*;

import java.io.Serializable;

/**
 * @Author Dooby Kim
 * @Date 2023/3/28 11:03 下午
 * @Version 1.0
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private String orderId;
    private Double price;
}
