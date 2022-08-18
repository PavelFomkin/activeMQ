package com.jms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class ActiveMqApplication2 {

	public static void main(String[] args) {
		SpringApplication.run(ActiveMqApplication2.class, args);
	}
}
