package org.corefx.callr.client.host;

import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.client.CallRClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.UUID;

@Slf4j
@SpringBootApplication(scanBasePackages = {
		"org.corefx.callr.client"
})
@EnableConfigurationProperties
public class CallRClientHostApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CallRClientHostApplication.class, args);
	}


	@Autowired
	CallRClientHostConfigurationProperties config;


	@Override
	public void run(String... args) throws Exception {

		CallRClient client = new CallRClient(config);
		client.onMessage(m -> log.info(m.toString()));
		client.connect();

		CallRMessage callRMessage = new CallRMessage();
		callRMessage.setSender(config.getId());
		client.send(callRMessage);

		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setSender(config.getId());
		requestMessage.setReceiver(config.getId());
		requestMessage.setRequest(UUID.randomUUID());
		requestMessage.setOperation("<none>");
		client.send(requestMessage);

		client.disconnect();
	}
}
