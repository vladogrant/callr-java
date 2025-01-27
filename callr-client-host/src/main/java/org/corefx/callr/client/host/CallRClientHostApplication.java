package org.corefx.callr.client.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CallRClientHostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallRClientHostApplication.class, args);
	}
}
