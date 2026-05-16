package com.rice.lfcdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rice.lfcdemo.mapper")
public class LfcDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(LfcDemoApplication.class, args);
	}

}
