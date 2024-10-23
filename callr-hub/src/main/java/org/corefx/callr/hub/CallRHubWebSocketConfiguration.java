package org.corefx.callr.hub;

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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	HttpRequestHandler callrHttpRequestHandler() {
		return new WebSocketHttpRequestHandler(callrWebSocketHandler());
	}


	@Bean
	HandlerAdapter callrHandlerAdapter() {
		return new HandlerAdapter() {

			final HttpRequestHandler httpRequestHandler = callrHttpRequestHandler();


			@Override
			public boolean supports(Object handler) {
				return true;
			}


			@Override
			public long getLastModified(jakarta.servlet.http.HttpServletRequest request, Object handler) {
				return 0;
			}


			@Override
			public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				httpRequestHandler.handleRequest(request, response);
				return null;
			}

		};
	}


	@Bean
	HandlerMapping callrHandlerMapping() {
		log.info("Creating CallR Handler Mapping Bean. Path: {}", uri.getPath());
		return new SimpleUrlHandlerMapping(Map.of(uri.getPath(), callrHandlerAdapter()), -1);
	}

}
