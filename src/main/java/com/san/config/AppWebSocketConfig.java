package com.san.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.san.socket.WebSocketHandler;

@Configuration
@ComponentScan({})

@EnableWebSocket // Required for WebSocketConfigurer to work

// Reference : https://spring.io/guides/gs/messaging-stomp-websocket/
// Reference : http://www.concretepage.com/spring-4/spring-4-websocket-sockjs-stomp-tomcat-example

// Note : We can use AbstractWebSocketMessageBrokerConfigurer and WebSocketConfigurer separately as well
// If we need to provide only STOMP support we can use only AbstractWebSocketMessageBrokerConfigurer
// If we need to provide only simple web-socket we can use only WebSocketConfigurer

public class AppWebSocketConfig implements WebSocketConfigurer {

	
	// To use Custom WebSocketHandler implements WebSocketConfigurer and define below function
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(), "/connectWebSocket");
	}
	
}