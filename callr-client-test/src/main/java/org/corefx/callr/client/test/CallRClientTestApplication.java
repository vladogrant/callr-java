package org.corefx.callr.client.test;

import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.client.CallRClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.TextMessage;

import java.net.URI;

@Slf4j
@SpringBootApplication
public class CallRClientTestApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CallRClientTestApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		CallRClient client = new CallRClient();
		client.onMessage(m -> log.info(m.getPayload().toString()));
		client.connect(URI.create("ws://localhost:8080/"));
		client.sendMessage(new TextMessage("Hello!"));
	}
}
