package org.cheonyakplanet.be.application.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.cheonyakplanet.be.domain.entity.comunity.PostCategory;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.config.NewsConfig;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

	private final RestTemplate restTemplate;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final NewsConfig newsConfig;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";
	private static final List<String> REAL_ESTATE_KEYWORDS = List.of(
		"부동산 정책", "주택 청약", "청약 당첨", "분양", "아파트 청약",
		"주택공급", "LH청약", "SH청약", "공공분양", "민간분양"
	);

	@Transactional
	public void crawlAndCreateNewsPosts() {
		log.info("부동산 뉴스 크롤링 시작");

		// 시스템 사용자로 인증 설정
		authenticateAsSystemUser();

		try {
			for (String keyword : REAL_ESTATE_KEYWORDS) {
				try {
					List<NewsItem> newsItems = fetchNewsFromNaver(keyword);

					for (NewsItem newsItem : newsItems) {
						if (!isDuplicatePost(newsItem.title)) {
							createNewsPost(newsItem);
							log.info("뉴스 포스트 생성: {}", newsItem.title);
						}
					}

					Thread.sleep(newsConfig.getApi().getDelayBetweenRequests());
				} catch (Exception e) {
					log.error("뉴스 크롤링 중 오류 발생 - 키워드: {}", keyword, e);
				}
			}
		} finally {
			// 인증 컨텍스트 정리
			SecurityContextHolder.clearContext();
		}

		log.info("부동산 뉴스 크롤링 완료");
	}

	private List<NewsItem> fetchNewsFromNaver(String query) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("X-Naver-Client-Id", getNaverClientId());
			headers.set("X-Naver-Client-Secret", getNaverClientSecret());

			String url = String.format("%s?query=%s&display=%d&start=1&sort=date",
				NAVER_NEWS_API_URL, java.net.URLEncoder.encode(query, "UTF-8"), newsConfig.getApi().getMaxResults());

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			return parseNaverNewsResponse(response.getBody());
		} catch (Exception e) {
			log.error("네이버 뉴스 API 호출 실패", e);
			return List.of();
		}
	}

	private List<NewsItem> parseNaverNewsResponse(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode items = root.get("items");

			return objectMapper.convertValue(items,
				objectMapper.getTypeFactory().constructCollectionType(List.class, NewsItem.class));
		} catch (Exception e) {
			log.error("뉴스 응답 파싱 실패", e);
			return List.of();
		}
	}

	private boolean isDuplicatePost(String title) {
		String cleanTitle = cleanHtmlTags(title);
		
		// 현재 인증된 사용자 정보 가져오기
		UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		return postRepository.findByTitleContainingAndUsernameAndDeletedAtIsNull(
				cleanTitle.substring(0, Math.min(cleanTitle.length(), 20)), currentUser.getUser().getUsername())
			.stream()
			.anyMatch(post -> post.getCreatedAt().isAfter(LocalDateTime.now().minusDays(1)));
	}

	private void createNewsPost(NewsItem newsItem) {
		String cleanTitle = cleanHtmlTags(newsItem.title);
		String cleanContent = buildNewsContent(newsItem);

		// 현재 인증된 사용자 정보 가져오기
		UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Post newsPost = Post.builder()
			.username(currentUser.getUser().getUsername())
			.title("[뉴스] " + cleanTitle)
			.content(cleanContent)
			.category(determineCategory(cleanTitle))
			.likes(0)
			.views(0L)
			.build();

		postRepository.save(newsPost);
	}

	private String buildNewsContent(NewsItem newsItem) {
		StringBuilder content = new StringBuilder();
		content.append("📰 **자동 수집된 부동산 뉴스입니다**\n\n");
		content.append("**제목:** ").append(cleanHtmlTags(newsItem.title)).append("\n\n");
		content.append("**요약:** ").append(cleanHtmlTags(newsItem.description)).append("\n\n");
		content.append("**출처:** ").append(newsItem.link).append("\n\n");
		content.append("**발행일:** ").append(formatPublishDate(newsItem.pubDate)).append("\n\n");
		content.append("---\n");
		content.append("*이 글은 시스템에 의해 자동으로 수집된 뉴스입니다. 자세한 내용은 원문을 확인해 주세요.*");

		return content.toString();
	}

	private PostCategory determineCategory(String title) {
		String lowerTitle = title.toLowerCase();

		if (lowerTitle.contains("청약") || lowerTitle.contains("분양") || lowerTitle.contains("공급")) {
			return PostCategory.SUBSCRIPTION_INFO;
		} else if (lowerTitle.contains("정책") || lowerTitle.contains("규제") || lowerTitle.contains("법")) {
			return PostCategory.INFO_SHARE;
		} else {
			return PostCategory.INFO_SHARE;
		}
	}

	private String cleanHtmlTags(String html) {
		if (html == null)
			return "";
		return html.replaceAll("<[^>]*>", "").replaceAll("&[^;]+;", "");
	}

	private String formatPublishDate(String pubDate) {
		try {
			return LocalDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		} catch (Exception e) {
			return pubDate;
		}
	}

	private void authenticateAsSystemUser() {
		try {
			// 시스템 계정으로 사용할 사용자 조회 (제공된 이메일 사용)
			User systemUser = userRepository.findByEmail("jjsus0307@gmail.com")
				.orElseThrow(() -> new RuntimeException("System user not found"));

			// UserDetailsImpl 생성
			UserDetailsImpl userDetails = new UserDetailsImpl(systemUser);

			// 인증 토큰 생성 및 Security Context에 설정
			UsernamePasswordAuthenticationToken authToken = 
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			
			SecurityContextHolder.getContext().setAuthentication(authToken);
			
			log.debug("시스템 사용자로 인증 완료: {}", systemUser.getEmail());
		} catch (Exception e) {
			log.error("시스템 사용자 인증 실패", e);
			throw new RuntimeException("Failed to authenticate as system user", e);
		}
	}

	/* ToDo : env에 값 설정     */
	private String getNaverClientId() {
		return System.getenv("NAVER_CLIENT_ID");
	}

	private String getNaverClientSecret() {
		return System.getenv("NAVER_CLIENT_SECRET");
	}

	public static class NewsItem {
		public String title;
		public String link;
		public String description;
		public String pubDate;
	}
}