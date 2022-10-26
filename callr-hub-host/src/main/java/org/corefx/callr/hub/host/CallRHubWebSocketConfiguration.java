package org.corefx.callr.hub.host;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.corefx.callr.hub.CallRHub;
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
import java.util.Map;

@Slf4j
@Configuration
public class CallRHubWebSocketConfiguration {

	@Bean
	CallRHub callrHub() {
		return new CallRHub();
	}


	@Bean
	WebSocketHandler callrHubWebSocketHandler() {
		log.info("Getting Callr Hub WebSocket Handler Bean");
		return callrHub().getWebSocketHandler();
	}


	;


	@Bean
	HttpRequestHandler webSocketHttpRequestHandler() {
		return new WebSocketHttpRequestHandler(callrHubWebSocketHandler());
	}


	@Bean
	HandlerAdapter webSocketHandlerAdapter() {
		return new HandlerAdapter() {

			@Override
			public boolean supports(Object handler) {
				return true;
			}


			@SneakyThrows
			@Override
			public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
				webSocketHttpRequestHandler().handleRequest(request, response);
				return null;
			}


			@Override
			public long getLastModified(HttpServletRequest request, Object handler) {
				return 0;
			}
		};
	}


	@Bean
	HandlerMapping WebSocketHandlerMapping() {
		log.info("Creating CallR Hub WebSocket Handler Mapping Bean");
		return new SimpleUrlHandlerMapping(Map.of("/", callrHubWebSocketHandler()), -1);
	}

}
