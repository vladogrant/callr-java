package org.corefx.callr.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.SenderMessage;
import org.corefx.callr.client.CallRClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.TextMessage;

import java.net.URI;
import java.util.UUID;

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

		UUID sender = UUID.randomUUID();

		SenderMessage senderMessage = new SenderMessage();
		senderMessage.setSender(sender);
		String payload = new ObjectMapper().writeValueAsString(senderMessage);
		client.sendMessage(new TextMessage(payload));

		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setSender(sender);
		requestMessage.setReceiver(sender);
		requestMessage.setRequestId(UUID.randomUUID());
		requestMessage.setOperation("Add");
		payload = new ObjectMapper().writeValueAsString(requestMessage);
		client.sendMessage(new TextMessage(payload));
		System.in.read();
	}
}
