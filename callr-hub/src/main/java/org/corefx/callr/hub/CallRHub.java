package org.corefx.callr.hub;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CallRHub {

	private final Map<String, WebSocketSession> sessions = new HashMap<>();

	private final WebSocketHandler webSocketHandler = new WebSocketHandler() {

		@Override
		public void afterConnectionEstablished(WebSocketSession session) {
			log.info("Connection established: [" + session.getId() + "]");
			sessions.put(session.getId(), session);
		}


		@Override
		@SneakyThrows
		public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
			log.info("Message received: [" + session.getId() + "] " + message);
			session.sendMessage(message);
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
	};


	public WebSocketHandler getWebSocketHandler() {
		return webSocketHandler;
	}
}
