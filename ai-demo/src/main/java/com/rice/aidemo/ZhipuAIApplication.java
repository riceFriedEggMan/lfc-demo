package com.rice.aidemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ZhipuAIApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZhipuAIApplication.class, args);
    }
}
