package org.cheonyakplanet.be.presentation.exception;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.cheonyakplanet.be.domain.exception.ErrorData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getDetails()
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail", errorData
        );

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.AUTH007;

        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                "관리자 권한이 필요한 API입니다"
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail", errorData
        );

        return ResponseEntity.ok(response);
    }

    // 2) 그 외 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleRuntimeException(RuntimeException e) {
        // 에러가 구체적인 CustomException이 아닌 경우, UNKNOWN_ERROR로 처리
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;

        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage()  // 혹은 별도의 default detail
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail",
                errorData
        );

        return ResponseEntity.ok(response);
    }

    // 3) 그 외 모든 예외(Exception)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                e.getMessage() // 필요한 경우
        );

        ApiResponse<ErrorData> response = new ApiResponse<>(
                "fail",
                errorData
        );

        return ResponseEntity.ok(response);
    }
}
