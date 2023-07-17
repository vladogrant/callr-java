package org.corefx.callr.hub;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Map;

@Slf4j
@Configuration
public class CallRHubWebSocketConfiguration {

	@Value("${callr.uri}")
	URI uri;


	@Bean
	WebSocketHandler callrWebSocketHandler() {
		log.info("Creating CallR WebSocket Handler");
		return new CallRHub();
	}


	@Bean
	HttpRequestHandler callrWebSocketHttpRequestHandler() {
		return new WebSocketHttpRequestHandler(callrWebSocketHandler());
	}


	@Bean
	HandlerAdapter callrWebSocketHandlerAdapter() {
		return new HandlerAdapter() {

			final HttpRequestHandler httpRequestHandler = callrWebSocketHttpRequestHandler();


			@Override
			public boolean supports(Object handler) {
				return true;
			}


			@SneakyThrows
			@Override
			public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
				httpRequestHandler.handleRequest(request, response);
				return null;
			}


			@Override
			public long getLastModified(HttpServletRequest request, Object handler) {
				return 0;
			}
		};
	}


	@Bean
	HandlerMapping callrWebSocketHandlerMapping() {
		log.info("Creating CallR WebSocket Handler Mapping Bean. Path: {}", uri.getPath());
		return new SimpleUrlHandlerMapping(Map.of(uri.getPath(), callrWebSocketHandlerAdapter()), -1);
	}

}
