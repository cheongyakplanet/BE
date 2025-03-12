package org.cheonyakplanet.be.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.domain.entity.Post;
import org.cheonyakplanet.be.domain.service.CommunityService;
import org.cheonyakplanet.be.domain.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class HomeController {

    private final SubscriptionService subscriptionService;
    private final CommunityService communityService;

    @GetMapping("/popular-locations")
    @Operation(summary = "가장 인기 있는 지역", description = "")
    public ResponseEntity<?> getPopularLocations() {
        Object result = subscriptionService.getPopularLocationList();
        return ResponseEntity.ok(new ApiResponse<>("sucess", result));
    }

    @GetMapping("/my-locations")
    @Operation(summary = "내 관심 지역")
    public ResponseEntity<?> getMyLocations(HttpServletRequest request) {
        List<String> interestLocals = subscriptionService.getInterestLocalsByEmail(request);
        return ResponseEntity.ok(new ApiResponse<>("sucess", "로그인 후 확인하세요"));
    }

    @GetMapping("/popular-content")
    @Operation(summary = "인기있는 게시글")
    public ResponseEntity<?> getPopularPosts() {
        List<Post> posts = communityService.getPopularPosts();
        return ResponseEntity.ok(new ApiResponse<>("sucess", posts));
    }
}
