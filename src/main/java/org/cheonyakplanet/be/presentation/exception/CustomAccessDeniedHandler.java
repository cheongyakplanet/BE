package org.cheonyakplanet.be.presentation.exception;

import java.io.IOException;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.domain.exception.ErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {

		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		ErrorData errorData = new ErrorData(
			"AUTH006",
			"접근 권한이 없습니다",
			accessDeniedException.getMessage());

		ApiResponse<ErrorData> apiResponse = new ApiResponse<>("fail", errorData);

		String json = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(json);
	}
}
