package com.nadia.mqhub.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.nadia.mqhub"})
@EnableAsync
public class NadiaMqhubApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(NadiaMqhubApplication.class, args);

		Test bean = run.getBean(Test.class);
		bean.send();
	}

}

