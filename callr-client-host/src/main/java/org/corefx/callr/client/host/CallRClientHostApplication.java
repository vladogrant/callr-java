package org.corefx.callr.client.host;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
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

	public CallRClientHostApplication(CallRClientHostConfigurationProperties config) {
		this.config = config;
	}


	public static void main(String[] args) {
		SpringApplication.run(CallRClientHostApplication.class, args);
	}

	private final	CallRClientHostConfigurationProperties config;

	private final Object waitHandle = new Object();


	@Override
	public void run(String... args) throws Exception {

		ObjectMapper json = new ObjectMapper();
		boolean indent = config.getJson() != null && config.getJson().isIndent();
		if(indent)
			json.enable(SerializationFeature.INDENT_OUTPUT);

		try(CallRClient client = new CallRClient(config)) {

			// Connect to the hub.
			client.connect();

			// Log any incoming message.
			client.onMessage(m -> {
				try {
					log.info("Message received: {}{}", indent ? System.lineSeparator() : "", json.writeValueAsString(m));
				}
				catch(JsonProcessingException e) {
					throw new RuntimeException(e);
				}
				synchronized(waitHandle) {
					waitHandle.notify();
				}
			});

			// Send a request message to ourselves...
			// It will go to the hub, get back to us and logged as configured above.
			RequestMessage m = new RequestMessage();
			m.setSender(config.getId());
			m.setReceiver(config.getId());
			m.setRequest(UUID.randomUUID());
			m.setOperation("<none>");
			client.send(m);
			log.info("Message sent: {}{}", indent ? System.lineSeparator() : "", json.writeValueAsString(m));

			// Wait up to 5 seconds for the message to come.
			synchronized(waitHandle) {
				waitHandle.wait(5000);
			}
			// client and connection will be automatically gracefully closed.
		}
		System.out.println("Press [Enter] to exit...");
		System.in.read();
	}
}
