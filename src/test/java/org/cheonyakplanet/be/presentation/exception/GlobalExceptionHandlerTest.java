package org.cheonyakplanet.be.presentation.exception;

import org.cheonyakplanet.be.infrastructure.jwt.JwtAuthorizationFilter;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup() {
        // Mock JWT validation to throw CustomException
        Mockito.when(jwtUtil.getTokenFromRequest(Mockito.any()))
                .thenThrow(new CustomException(ErrorCode.AUTH005, "Token validation failed"));
    }

    @Test
    @DisplayName("Test GlobalExceptionHandler for CustomException")
    void testCustomExceptionHandler() throws Exception {
        mockMvc.perform(get("/api/protected-endpoint")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.data.code").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.data.message").value("Token validation failed"));
    }
}