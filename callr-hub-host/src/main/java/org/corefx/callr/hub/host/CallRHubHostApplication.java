package org.corefx.callr.hub.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
		exclude = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class}
		, scanBasePackages = {"org.corefx.callr.hub"}
)
public class CallRHubHostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallRHubHostApplication.class, args);
	}
}
