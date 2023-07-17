package org.corefx.callr.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.CallRMessage;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
public class CallRClient {

	private URI uri;

	@Getter
	private final UUID id;

	private WebSocketSession session;

	private Consumer<CallRMessage> messageHandler;


	public CallRClient(UUID id, URI uri) {
		this.id = id;
		this.uri = uri;
	}


	public void onMessage(Consumer<CallRMessage> messageHandler) {
		this.messageHandler = messageHandler;
	}


	public void connect() {
		StandardWebSocketClient client = new StandardWebSocketClient();
		try {
			session = client.doHandshake(new WebSocketHandler() {

				@Override
				public void afterConnectionEstablished(WebSocketSession session) {
					session.setTextMessageSizeLimit(10 * 1024 * 1024);
					session.setBinaryMessageSizeLimit(10 * 1024 * 1024);
					log.info("Connection established: [" + session.getId() + "]");
					CallRMessage m = new CallRMessage(id);
					String payload;
					try {
						payload = new ObjectMapper().writeValueAsString(m);
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
					CallRMessage m;
					try {
						m = new ObjectMapper().readValue(message.getPayload().toString(), CallRMessage.class);
					}
					catch(JsonProcessingException e) {
						log.error(e.getMessage(), e);
						throw new RuntimeException(e);
					}
					if(messageHandler != null)
						messageHandler.accept(m);
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
			}, new WebSocketHttpHeaders(), uri).get();
		}
		catch(InterruptedException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		catch(ExecutionException e) {
			throw new RuntimeException(e);
		}
	}


	public void send(CallRMessage m) {
		String payload = null;
		try {
			payload = new ObjectMapper().writeValueAsString(m);
		}
		catch(JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		try {
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
}
