package org.corefx.callr.client.test;

import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.client.CallRClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

		CallRClient client = new CallRClient(UUID.randomUUID(), URI.create("ws://localhost:8080/"));
		client.onMessage(m -> log.info(m.getSender().toString()));
		client.connect();

		UUID sender = UUID.randomUUID();

		CallRMessage callRMessage = new CallRMessage();
		callRMessage.setSender(sender);
		client.send(callRMessage);

		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setSender(sender);
		requestMessage.setReceiver(sender);
		requestMessage.setRequest(UUID.randomUUID());
		requestMessage.setOperation("Add");
		client.send(requestMessage);
		System.in.read();
	}
}
