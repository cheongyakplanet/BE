package org.cheonyakplanet.be.presentation.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public JwtExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException ex) {
            // 커스텀 예외 처리
            setErrorResponse(response, ex);
        } catch (Exception ex) {
            // 기타 예외 처리
            setErrorResponse(response,
                    new CustomException(ErrorCode.AUTH005, "유효하지 않은 토큰"));
        }
    }

    private void setErrorResponse(HttpServletResponse response, CustomException ex) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorCode errorCode = ex.getErrorCode();
        ErrorData errorData = new ErrorData(
                errorCode.getCode(),
                errorCode.getMessage(),
                ex.getDetails()
        );

        ApiResponse<ErrorData> apiResponse = new ApiResponse<>("fail", errorData);

        String json = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().write(json);
    }
}
