package org.corefx.callr.hub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.RequestMessage;
import org.corefx.callr.ResponseMessage;
import org.corefx.callr.RpcMessage;
import org.corefx.callr.configuration.GlobalConfigurationProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.socket.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class CallRHub implements WebSocketHandler {

	private final Map<UUID, WebSocketSession> sessions = new HashMap<>();
	private final ObjectMapper json = new ObjectMapper();
	private final boolean indent;


	public CallRHub(GlobalConfigurationProperties config) {
		this.indent = config.getJson() != null && config.getJson().isIndent();
		if(this.indent)
			json.enable(SerializationFeature.INDENT_OUTPUT);
	}


	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		session.setTextMessageSizeLimit(10 * 1024 * 1024);
		session.setBinaryMessageSizeLimit(10 * 1024 * 1024);
		log.info("Connection established: [{}]", session.getId());
	}


	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
		log.info("Message received: [{}]", session.getId());
		String payload = message.getPayload().toString();
		log.debug("{}{}", indent ? System.lineSeparator() : "", payload);

		CallRMessage m;
		try {
			m = json.readValue(payload, CallRMessage.class);
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
					log.warn("Failed to close an abandoned session: [{}]", existing.getId());
					log.debug(e.getMessage(), e);
				}
			}
			sessions.put(sender, session);
			return;
		}
		if(RpcMessage.class.isAssignableFrom(m.getClass())) {
			RpcMessage rpcm = (RpcMessage) m;
			UUID receiver = rpcm.getReceiver();
			if(RequestMessage.class.equals(m.getClass())) {
				RequestMessage rm = (RequestMessage) m;
				Principal sessionPrincipal = session.getPrincipal();
				if(sessionPrincipal != null && Authentication.class.isAssignableFrom(sessionPrincipal.getClass())) {
					Authentication auth = (Authentication)sessionPrincipal;
					Object principalObject = auth.getPrincipal();
					if(principalObject != null && UserDetails.class.isAssignableFrom(principalObject.getClass())) {
						UserDetails user = (UserDetails)auth.getPrincipal();
						if(user.getAuthorities() != null)
							rm.setAuthorities(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
					}
				}
				try {
					payload = json.writeValueAsString(m);
					message = new TextMessage(payload);
				}
				catch(JsonProcessingException e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
			}
			WebSocketSession receiverSession = sessions.get(receiver);
			if(receiverSession != null) {
				try {
					receiverSession.sendMessage(message);
					log.info("Message sent: [{}]", receiverSession.getId());
					log.debug("{}{}", indent ? System.lineSeparator() : "", payload);
				}
				catch(IOException e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
				return;
			}
			RuntimeException ex = new RuntimeException("Receiver " + receiver + " is not connected to the hub");
			ResponseMessage response = new ResponseMessage();
			response.setReceiver(rpcm.getSender());
			response.setRequest(rpcm.getRequest());
			response.setException(ex);
			try(ByteArrayOutputStream os = new ByteArrayOutputStream(); ObjectOutputStream ow = new ObjectOutputStream(os)) {
				ow.writeObject(ex);
				response.setExceptionData(os.toByteArray());
			}
			catch(IOException e) {
				throw new RuntimeException(e);
			}
			try {
				payload = json.writeValueAsString(response);
				session.sendMessage(new TextMessage(payload));
				log.info("Message sent: [{}]", session.getId());
				log.debug("{}{}", indent ? System.lineSeparator() : "", payload);
			}
			catch(IOException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
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
