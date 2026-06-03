package com.rice.lfcdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.rice.lfcdemo.mapper")
@EnableScheduling
public class XtimerApplication {
    public static void main(String[] args) {
        SpringApplication.run(XtimerApplication.class, args);
    }
}
