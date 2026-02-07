package com.globant.interview.david.msdavidmobilephone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.globant.interview.david.msdavidmobilephone.infrastructure.output.client")
public class MsdavidmobilephoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsdavidmobilephoneApplication.class, args);
	}

}
