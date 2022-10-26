package org.corefx.callr.hub.host;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication
public class CallRHubHostApplication {

	public static void main(String[] args) {
		SpringApplication.run(CallRHubHostApplication.class, args);
	}

}
