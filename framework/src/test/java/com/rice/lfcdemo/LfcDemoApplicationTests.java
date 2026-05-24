package com.rice.lfcdemo;

import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootTest
class LfcDemoApplicationTests {

	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
		List<User> list = userService.list();
		System.out.println(list);
	}

	@Test
	void passwordEncoder() {
		System.out.println(passwordEncoder.encode("1234"));
	}

}
