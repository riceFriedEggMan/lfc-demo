package com.rice.msg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rice.msg.mapper")
public class MsgCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgCenterApplication.class, args);
    }
}
