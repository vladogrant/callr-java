package org.corefx.callr.client.host;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.client.CallRClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties
@EnableScheduling
@Component
public class CallRClientScheduledTask {

	private final CallRClientHostConfigurationProperties config;
	

	@Scheduled(fixedRate = Long.MAX_VALUE)
	public void run() throws Exception {
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

			// client and connection will be automatically gracefully closed.
		}

	}

}
