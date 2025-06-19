package org.cheonyakplanet.be.infrastructure.jwt;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {
	private final JwtUtil jwtUtil;

	public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
		WebSocketHandler wsHandler, Map<String, Object> attributes) {
		String token = ((ServletServerHttpRequest)request).getServletRequest().getParameter("token");
		if (!StringUtils.hasText(token) || !jwtUtil.validateToken(token)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		String userEmail = jwtUtil.getUserInfoFromToken(token).getSubject();
		attributes.put("userEmail", userEmail);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

	}
}
