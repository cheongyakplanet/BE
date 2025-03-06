package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDetailDTO;
import org.cheonyakplanet.be.domain.service.InfoService;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final InfoService infoService;

    @GetMapping("/success")
    @ResponseBody
    public ResponseEntity<ApiResponse<Object>> getSuccess() {
        ApiResponse<Object> response = new ApiResponse<>("success", "test");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fail-auth")
    @ResponseBody
    public ResponseEntity<ApiResponse<?>> getFailAuth() {
        throw new CustomException(ErrorCode.AUTH001, "로그인 필요 또는 토큰 만료");
    }

    @GetMapping("/member/login-page")
    public String loginPage() {
        return "login";
    }

}
