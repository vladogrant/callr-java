package org.corefx.callr.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.configuration.ClientConfigurationProperties;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
public class CallRClient {

	private final ClientConfigurationProperties config;

	@Getter
	private final UUID id;

	private WebSocketSession session;

	private Consumer<CallRMessage> messageHandler;


	public CallRClient(ClientConfigurationProperties config) {
		this.config = config;
		this.id = config.getId();
	}


	public void onMessage(Consumer<CallRMessage> messageHandler) {
		this.messageHandler = messageHandler;
	}


	@SneakyThrows
	public void connect() {

		StandardWebSocketClient client = new StandardWebSocketClient();

		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		sslContextBuilder.loadTrustMaterial(
				config.getSsl().getTrustStore().getFile().getURL(), config.getSsl().getTrustStore().getPassword().toCharArray());
		SSLContext sslContext = sslContextBuilder.build();
		client.setSslContext(sslContext);

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
		if("key".equals(config.getAuthentication().getType()))
			headers.add(config.getAuthentication().getKey().getHeader(), config.getAuthentication().getKey().getSecret());
		else if("basic".equals(config.getAuthentication().getType())) {
			String plainCreds = config.getAuthentication().getBasic().getId() + ":" + config.getAuthentication().getBasic().getSecret();
			String creds = Base64.getEncoder().encodeToString(plainCreds.getBytes());
			headers.add("Authorization", "Basic " + creds);
		}

		try {
			session = client.execute(new CallRClientWebSocketHandler(), headers, config.getUri()).get();
		}
		catch(InterruptedException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		catch(ExecutionException e) {
			throw new RuntimeException(e);
		}
	}


	@SneakyThrows
	public void disconnect() {
		session.close();
	}


	public void send(CallRMessage m) {
		String payload;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			payload = objectMapper.writeValueAsString(m);
			TextMessage message = new TextMessage(payload);
			session.sendMessage(message);
			log.info("Message sent: [" + session.getId() + "] " + message);
			log.debug(payload);
		}
		catch(IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	private class CallRClientWebSocketHandler implements WebSocketHandler {

		@Override
		public void afterConnectionEstablished(WebSocketSession session) {
			session.setTextMessageSizeLimit(10 * 1024 * 1024);
			session.setBinaryMessageSizeLimit(10 * 1024 * 1024);
			log.info("Connection established: [" + session.getId() + "]");
			CallRMessage m = new CallRMessage(config.getId());
			String payload;
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				payload = objectMapper.writeValueAsString(m);
			}
			catch(JsonProcessingException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			try {
				session.sendMessage(new TextMessage(payload));
			}
			catch(IOException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}


		@Override
		public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
			log.info("Message received: [" + session.getId() + "] " + message);
			String payload = message.getPayload().toString();
			log.debug(payload);
			CallRMessage m;
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				m = objectMapper.readValue(payload, CallRMessage.class);
				if(messageHandler != null)
					messageHandler.accept(m);
			}
			catch(JsonProcessingException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}


		@Override
		public void handleTransportError(WebSocketSession session, Throwable exception) {
			log.error(exception.getMessage(), exception);
		}


		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
			log.info("Connection closed: [" + session.getId() + "]");
			log.info(closeStatus.toString());
		}


		@Override
		public boolean supportsPartialMessages() {
			return false;
		}
	}
}
