package org.cheonyakplanet.be.infrastructure.config;

import org.cheonyakplanet.be.infrastructure.ChatWebSocketHandler;
import org.cheonyakplanet.be.infrastructure.jwt.JwtHandshakeInterceptor;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
	private final ChatWebSocketHandler chatHandler;
	private final JwtUtil jwtUtil;

	public WebSocketConfig(ChatWebSocketHandler chatHandler, JwtUtil jwtUtil) {
		this.chatHandler = chatHandler;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(chatHandler, "/ws/chat")
			.addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
			.setAllowedOrigins("*");
	}
}
