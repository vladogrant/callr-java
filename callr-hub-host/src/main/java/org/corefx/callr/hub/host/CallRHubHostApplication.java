package org.corefx.callr.hub.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"org.corefx.callr.hub"
})
public class CallRHubHostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallRHubHostApplication.class, args);
	}

}
