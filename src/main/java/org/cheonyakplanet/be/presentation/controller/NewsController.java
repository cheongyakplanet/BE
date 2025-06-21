package org.cheonyakplanet.be.presentation.controller;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.service.NewsService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Tag(name = "뉴스 관리", description = "뉴스 크롤링 및 자동 포스트 생성 API")
public class NewsController extends BaseController {

	private final NewsService newsService;

	@PostMapping("/crawl")
	@Operation(summary = "수동 뉴스 크롤링", description = "관리자용 수동 뉴스 크롤링 및 포스트 생성")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<String>> manualNewsCrawl(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			log.info("수동 뉴스 크롤링 요청 - 사용자: {}", userDetails.getUser().getEmail());
			newsService.crawlAndCreateNewsPosts();
			return ResponseEntity.ok(
				ApiResponse.<String>builder()
					.status("NEWS001")
					.data("뉴스 크롤링이 성공적으로 완료되었습니다.")
					.build()
			);
		} catch (Exception e) {
			log.error("수동 뉴스 크롤링 실패", e);
			return ResponseEntity.badRequest().body(
				ApiResponse.<String>builder()
					.status("NEWS002")
					.data("뉴스 크롤링 중 오류가 발생했습니다: " + e.getMessage())
					.build()
			);
		}
	}
}