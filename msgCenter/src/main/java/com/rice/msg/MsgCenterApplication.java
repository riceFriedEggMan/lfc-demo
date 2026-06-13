package com.rice.msg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@MapperScan("com.rice.msg.mapper")
@ComponentScan(basePackages = "com.rice")
@EnableScheduling
public class MsgCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgCenterApplication.class, args);
    }
}
