package org.corefx.callr.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.ssl.PrivateKeyDetails;
import org.apache.hc.core5.ssl.PrivateKeyStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.corefx.callr.CallRMessage;
import org.corefx.callr.configuration.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
public class CallRClient implements AutoCloseable {

	private final ClientConfigurationProperties config;

	private static final boolean useKeystores = false;

	@Getter
	private final UUID id;

	private WebSocketSession session;

	private Consumer<CallRMessage> messageHandler;

	private static final ObjectMapper json = new ObjectMapper();
	private final boolean indent;


	public CallRClient(ClientConfigurationProperties config) {
		this.config = config;
		this.id = config.getId();
		this.indent = config.getJson() != null && config.getJson().isIndent();
		if(this.indent)
			json.enable(SerializationFeature.INDENT_OUTPUT);
	}


	public void onMessage(Consumer<CallRMessage> messageHandler) {
		this.messageHandler = messageHandler;
	}


	@SneakyThrows
	public void connect() {

		StandardWebSocketClient client = new StandardWebSocketClient();

		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		TrustStoreConfigurationProperties trustStoreConfig = config.getSsl().getTrustStore();
		if(useKeystores) {
			KeyStore trustStore = KeyStore.getInstance("jks");
			trustStore.load(trustStoreConfig.getFile().getInputStream(), trustStoreConfig.getPassword().toCharArray());
			sslContextBuilder.loadTrustMaterial(trustStore, (certs, s) -> {
				return true; // NOTE: Only for testing. Not for production use.
			});
		}
		else {
			sslContextBuilder.loadTrustMaterial(
					trustStoreConfig.getFile().getURL(),
					trustStoreConfig.getPassword().toCharArray());
		}

		WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

		AuthenticationConfigurationProperties authConfig = config.getAuthentication();
		if("key".equals(authConfig.getType())) {
			KeyAuthenticationConfigurationProperties keyAuth = authConfig.getKey();
			headers.add(keyAuth.getHeader(), keyAuth.getSecret());
		}
		else if("basic".equals(authConfig.getType())) {
			BasicAuthenticationConfigurationProperties basicAuth = authConfig.getBasic();
			String id = basicAuth.getId() != null ? basicAuth.getId() : this.id.toString().toUpperCase();
			String plainCreds = id + ":" + basicAuth.getSecret();
			String creds = Base64.getEncoder().encodeToString(plainCreds.getBytes());
			headers.add("Authorization", "Basic " + creds);
		}
		else if("ssl".equals(authConfig.getType())) {

			SslAuthenticationConfigurationProperties sslAuthConfig = authConfig.getSsl();
			KeyStoreConfigurationProperties keyStoreConfig = sslAuthConfig.getKeyStore();

			if(useKeystores) {
				KeyStore keyStore = KeyStore.getInstance("jks");
				keyStore.load(keyStoreConfig.getFile().getInputStream(), keyStoreConfig.getPassword().toCharArray());
				sslContextBuilder.loadKeyMaterial(
						keyStore, keyStoreConfig.getPassword().toCharArray(), (map, sslParameters) -> id.toString().toUpperCase());
			}
			else {
				sslContextBuilder.loadKeyMaterial(
						keyStoreConfig.getFile().getURL(),
						keyStoreConfig.getPassword().toCharArray(),
						keyStoreConfig.getPassword().toCharArray());
			}
		}

		SSLContext sslContext = sslContextBuilder.build();
		client.setSslContext(sslContext);

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
			payload = json.writeValueAsString(m);
			TextMessage message = new TextMessage(payload);
			session.sendMessage(message);
			log.info("Message sent: [{}]", session.getId());
			log.debug("{}{}", indent ? System.lineSeparator() : "", payload);
		}
		catch(IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


	@Override
	@SneakyThrows
	public void close() {
		session.close();
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
				payload = json.writeValueAsString(m);
			}
			catch(JsonProcessingException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
			try {
				session.sendMessage(new TextMessage(payload));
				log.info("Message sent: [{}]", session.getId());
				log.debug("{}{}", indent ? System.lineSeparator() : "", payload);
			}
			catch(IOException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}


		@Override
		public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
			log.info("Message received: [" + session.getId() + "]");
			String payload = message.getPayload().toString();
			log.debug((indent ? System.lineSeparator() : "") + payload);
			CallRMessage m;
			try {
				m = json.readValue(payload, CallRMessage.class);
				if(messageHandler != null)
					messageHandler.accept(m);
			}
			catch(JsonProcessingException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}


		@SuppressWarnings("NullableProblems")
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
