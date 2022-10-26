package org.corefx.callr.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.function.Consumer;

@Slf4j
public class CallRClient {

	private StandardWebSocketClient client;
	private WebSocketSession session;


	@SneakyThrows
	public void connect(URI uri) {
		client = new StandardWebSocketClient();
		session = client.doHandshake(new WebSocketHandler() {

			@Override
			public void afterConnectionEstablished(WebSocketSession session) {
				log.info("Connection established: [" + session.getId() + "]");
			}


			@Override
			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
				log.info("Message received: [" + session.getId() + "] " + message);
				if(consumer != null)
					consumer.accept(message);
			}


			@Override
			public void handleTransportError(WebSocketSession session, Throwable exception) {

			}


			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
				log.info("Connection closed: [" + session.getId() + "]");
			}


			@Override
			public boolean supportsPartialMessages() {
				return false;
			}
		}, new WebSocketHttpHeaders(), uri).get();
	}

	Consumer<WebSocketMessage<?>> consumer;


	public void onMessage(Consumer<WebSocketMessage<?>> consumer) {
		this.consumer = consumer;
	}

	@SneakyThrows
	public void sendMessage(WebSocketMessage<?> message) {
		log.info("Sending message: " + message);
		session.sendMessage(message);
	}
}
