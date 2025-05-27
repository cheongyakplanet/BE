package org.cheonyakplanet.be.presentation.controller;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

	/**
	 * 성공 응답 생성 (데이터 포함)
	 */
	protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
		return ResponseEntity.ok(new ApiResponse<>("success", data));
	}

	/**
	 * 성공 응답 생성 (메시지만)
	 */
	protected ResponseEntity<ApiResponse<String>> success(String message) {
		return ResponseEntity.ok(new ApiResponse<>("success", message));
	}

	/**
	 * 실패 응답 생성
	 */
	protected <T> ResponseEntity<ApiResponse<T>> fail(T data) {
		return ResponseEntity.ok(new ApiResponse<>("fail", data));
	}
}
