package org.corefx.callr.hub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.ResponseMessage;
import org.corefx.callr.RpcMessage;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class CallRHub implements WebSocketHandler {

	private final Map<UUID, WebSocketSession> sessions = new HashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		session.setTextMessageSizeLimit(10*1024*1024);
		session.setBinaryMessageSizeLimit(10*1024*1024);
		log.info("Connection established: [" + session.getId() + "]");
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
		if(CallRMessage.class.equals(m.getClass())) {
			UUID sender = m.getSender();
			WebSocketSession existing = sessions.get(sender);
			if(existing != null && existing != session) {
				try {
					existing.close();
				}
				catch(Exception e) {
					log.warn("Failed to close an abandoned session: [" + existing.getId() + "]");
					log.debug(e.getMessage(), e);
				}
			}
			sessions.put(sender, session);
			return;
		}
		RpcMessage rpcm = (RpcMessage) m;
		UUID receiver = rpcm.getReceiver();
		WebSocketSession receiverSession = sessions.get(receiver);
		if(receiverSession != null) {
			try {
				receiverSession.sendMessage(message);
			}
			catch(IOException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			return;
		}
		RuntimeException ex = new RuntimeException("Receiver " + receiver + " is not registered within the hub");
		ResponseMessage response = new ResponseMessage();
		response.setReceiver(m.getSender());
/*
			ExceptionInfo exi = new ExceptionInfo();
			try {
				exi.setData(new ObjectMapper().writeValueAsString(ex));
			}
			catch(JsonProcessingException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			exi.setType(ex.getClass().getName());
			response.setException(exi);
*/
		response.setException(ex);
		try {
			session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
		}
		catch(IOException e) {
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
		log.info("Connection closed: [{}]", session.getId());
		log.info(closeStatus.toString());
	}


	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

}
