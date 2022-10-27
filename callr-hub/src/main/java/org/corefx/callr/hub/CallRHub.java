package org.corefx.callr.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.ExceptionInfo;
import org.corefx.callr.ResponseMessage;
import org.corefx.callr.RpcMessage;
import org.corefx.callr.SenderMessage;
import org.springframework.web.socket.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class CallRHub {

	private final Map<UUID, WebSocketSession> sessions = new HashMap<>();

	private final WebSocketHandler webSocketHandler = new WebSocketHandler() {

		@Override
		public void afterConnectionEstablished(WebSocketSession session) {
			log.info("Connection established: [" + session.getId() + "]");
		}


		@Override
		@SneakyThrows
		public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
			log.info("Message received: [" + session.getId() + "] " + message);
			SenderMessage m = new ObjectMapper().readValue(message.getPayload().toString(), SenderMessage.class);
			if(SenderMessage.class.equals(m.getClass())) {
				UUID sender = m.getSender();
				if(!sessions.containsKey(sender))
					sessions.put(sender, session);
			}
			else {
				RpcMessage rpcm = (RpcMessage) m;
				UUID receiver = rpcm.getReceiver();
				WebSocketSession receiverSession = sessions.get(receiver);
				if(receiverSession == null) {
					RuntimeException ex = new RuntimeException("Receiver " + receiver + " is not registered within the hub");
					ResponseMessage response = new ResponseMessage();
					response.setReceiver(m.getSender());
					ExceptionInfo exi = new ExceptionInfo();
					exi.setData(new ObjectMapper().writeValueAsString(ex));
					exi.setType(ex.getClass().getName());
					response.setException(exi);
					session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
				}
				else {
					receiverSession.sendMessage(message);
				}
			}
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
