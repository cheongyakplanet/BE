package org.cheonyakplanet.be.application.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.cheonyakplanet.be.domain.entity.comunity.PostCategory;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.config.NewsConfig;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

	private final WebClient webClient = WebClient.builder()
		.baseUrl(NAVER_NEWS_API_URL)
		.defaultHeader("User-Agent", "Mozilla/5.0 (compatible; CheonYakPlanet/1.0)")
		.build();

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final NewsConfig newsConfig;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

	// 부동산 관련 키워드를 더 세분화하고 확장
	private static final List<String> REAL_ESTATE_KEYWORDS = List.of(
		"부동산", "주택", "아파트", "오피스텔", "상가", "토지",
		"청약", "분양", "임대", "전세", "월세", "매매",
		"부동산정책", "주택정책", "임대차", "전월세", "재건축", "재개발",
		"LH", "SH", "한국토지주택공사", "주택도시보증공사",
		"부동산시장", "주택시장", "아파트값", "집값", "주택가격",
		"국토교통부", "가계대출", "주담대"
	);

	// 강한 광고성 키워드 (반드시 제외)
	private static final Set<String> STRONG_AD_KEYWORDS = Set.of(
		"할인", "쿠폰", "이벤트", "프로모션", "무료체험", "가입하면",
		"당첨자발표", "추첨", "경품", "선착순", "한정판매",
		"최대할인", "특가", "세일", "런칭기념", "오픈기념"
	);

	// 약한 광고성 키워드 (점수 기반으로 판단)
	private static final Set<String> WEAK_AD_KEYWORDS = Set.of(
		"광고", "홍보", "마케팅", "브랜드", "론칭", "오픈",
		"카페", "맛집", "쇼핑", "패션", "뷰티", "여행",
		"펜션", "호텔", "리조트", "상품권"
	);

	// 신뢰할 수 있는 뉴스 도메인을 확장
	private static final Set<String> ALLOWED_NEWS_DOMAINS = Set.of(
		"news.naver.com", "land.naver.com", "realty.chosun.com",
		"mk.co.kr", "chosun.com", "joongang.co.kr", "hani.co.kr", "donga.com", "ebn.co.kr", "ibabynews.com",
		"etnews.com", "yna.co.kr", "newsis.com", "ytn.co.kr", "sbs.co.kr", "seoul.co.kr",
		"kbs.co.kr", "mbc.co.kr", "jtbc.co.kr", "hankyung.com", "mt.co.kr",
		"edaily.co.kr", "news1.kr", "newspim.com", "biz.chosun.com", "widedaily.com",
		"realestate.daum.net", "land.seoul.go.kr", "molit.go.kr", "news.sbs.co.kr", "sedaily.com", "enewstoday.co.kr"
	);

	// 광고성 URL 패턴
	private static final List<Pattern> AD_URL_PATTERNS = Arrays.asList(
		Pattern.compile(".*promotion.*", Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*event.*", Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*ad.*", Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*banner.*", Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*campaign.*", Pattern.CASE_INSENSITIVE),
		Pattern.compile(".*allurekorea.*", Pattern.CASE_INSENSITIVE)

	);

	@Transactional
	public void crawlAndCreateNewsPosts() {
		log.info("부동산 뉴스 크롤링 및 일간 요약 생성 시작");
		authenticateAsSystemUser();

		try {
			if (isDailySummaryAlreadyCreated()) {
				log.info("오늘 일간 부동산 뉴스 요약이 이미 생성되었습니다.");
				return;
			}

			List<NewsItem> allNewsItems = new ArrayList<>();
			for (String keyword : REAL_ESTATE_KEYWORDS) {
				try {
					allNewsItems.addAll(fetchNewsFromNaver(keyword));
					Thread.sleep(newsConfig.getApi().getDelayBetweenRequests());
				} catch (Exception e) {
					log.error("뉴스 크롤링 중 오류 발생 - 키워드: {}", keyword, e);
				}
			}

			log.info("총 수집된 기사 수: {}", allNewsItems.size());

			List<NewsItem> filteredNews = filterAndDeduplicateNewsImproved(allNewsItems);
			log.info("필터링 후 기사 수: {}", filteredNews.size());

			if (!filteredNews.isEmpty()) {
				createDailySummaryPost(filteredNews);
				log.info("일간 부동산 뉴스 요약 포스트 생성 완료 - {} 개 기사 요약", filteredNews.size());
			} else {
				// 필터링이 너무 엄격한 경우 대안 제공
				log.warn("필터링 후 기사가 없습니다. 완화된 조건으로 재시도합니다.");
				List<NewsItem> relaxedFilteredNews = filterWithRelaxedConditions(allNewsItems);
				if (!relaxedFilteredNews.isEmpty()) {
					createDailySummaryPost(relaxedFilteredNews);
					log.info("완화된 조건으로 요약 포스트 생성 완료 - {} 개 기사 요약", relaxedFilteredNews.size());
				} else {
					log.info("완화된 조건으로도 요약할 만한 부동산 뉴스가 없습니다.");
				}
			}

		} finally {
			SecurityContextHolder.clearContext();
		}

		log.info("부동산 뉴스 크롤링 및 일간 요약 생성 완료");
	}

	// 개선된 필터링 로직
	private List<NewsItem> filterAndDeduplicateNewsImproved(List<NewsItem> newsItems) {
		Set<String> seenTitles = new HashSet<>();
		List<NewsItem> filtered = new ArrayList<>();

		for (NewsItem item : newsItems) {
			// 1단계: 기본 유효성 검사
			if (item.title == null || item.description == null) {
				continue;
			}

			String cleanTitle = cleanHtmlTags(item.title);
			String cleanDesc = cleanHtmlTags(item.description);
			String combinedText = (cleanTitle + " " + cleanDesc).toLowerCase();

			// 2단계: 중복 제거 (제목 기준)
			if (seenTitles.contains(cleanTitle)) {
				continue;
			}

			// 3단계: 강한 광고성 키워드 제거 (절대 제외)
			if (containsStrongAdKeywords(combinedText)) {
				log.debug("강한 광고성 키워드로 인해 제외: {}", cleanTitle);
				continue;
			}

			// 4단계: URL 기반 광고 필터링
			if (isAdUrl(item.originallink) || isAdUrl(item.link)) {
				log.debug("광고성 URL로 인해 제외: {}", cleanTitle);
				continue;
			}

			// 5단계: 도메인 검증
			String domain = extractDomain(item.originallink != null ? item.originallink : item.link);
			if (!ALLOWED_NEWS_DOMAINS.contains(domain)) {
				log.debug("허용되지 않은 도메인으로 인해 제외: {} ({})", cleanTitle, domain);
				continue;
			}

			// 6단계: 부동산 관련성 검사
			if (!isRealEstateRelated(combinedText)) {
				log.debug("부동산 관련성 부족으로 인해 제외: {}", cleanTitle);
				continue;
			}

			// 7단계: 콘텐츠 품질 검사
			if (cleanDesc.length() < 30) {
				log.debug("내용이 너무 짧아서 제외: {}", cleanTitle);
				continue;
			}

			// 8단계: 광고 점수 계산 (약한 광고성 키워드)
			int adScore = calculateAdScore(combinedText);
			int relevanceScore = calculateRelevanceScore(combinedText);

			// 관련성 점수가 광고 점수보다 높으면 포함
			if (relevanceScore > adScore) {
				seenTitles.add(cleanTitle);
				filtered.add(item);
				log.debug("포함된 기사: {} (관련성: {}, 광고성: {})", cleanTitle, relevanceScore, adScore);
			} else {
				log.debug("광고성 점수가 높아서 제외: {} (관련성: {}, 광고성: {})", cleanTitle, relevanceScore, adScore);
			}
		}

		return filtered.stream()
			.limit(newsConfig.getScheduling().getMaxDailyPosts())
			.collect(Collectors.toList());
	}

	// 완화된 조건으로 필터링
	private List<NewsItem> filterWithRelaxedConditions(List<NewsItem> newsItems) {
		Set<String> seenTitles = new HashSet<>();
		List<NewsItem> filtered = new ArrayList<>();

		for (NewsItem item : newsItems) {
			if (item.title == null || item.description == null) {
				continue;
			}

			String cleanTitle = cleanHtmlTags(item.title);
			String cleanDesc = cleanHtmlTags(item.description);
			String combinedText = (cleanTitle + " " + cleanDesc).toLowerCase();

			// 중복 제거
			if (seenTitles.contains(cleanTitle)) {
				continue;
			}

			// 강한 광고성 키워드만 제외 (약한 광고성 키워드는 허용)
			if (containsStrongAdKeywords(combinedText)) {
				continue;
			}

			// 최소한의 부동산 관련성 검사
			if (hasMinimalRealEstateRelevance(combinedText)) {
				seenTitles.add(cleanTitle);
				filtered.add(item);
			}
		}

		return filtered.stream()
			.limit(Math.max(3, newsConfig.getScheduling().getMaxDailyPosts() / 2)) // 최소 3개는 확보
			.collect(Collectors.toList());
	}

	// 강한 광고성 키워드 포함 여부
	private boolean containsStrongAdKeywords(String text) {
		return STRONG_AD_KEYWORDS.stream()
			.anyMatch(keyword -> text.contains(keyword.toLowerCase()));
	}

	// URL이 광고성인지 검사
	private boolean isAdUrl(String url) {
		if (url == null)
			return false;
		return AD_URL_PATTERNS.stream()
			.anyMatch(pattern -> pattern.matcher(url).matches());
	}

	// 부동산 관련성 검사 (강화)
	private boolean isRealEstateRelated(String text) {
		long matchCount = REAL_ESTATE_KEYWORDS.stream()
			.filter(keyword -> text.contains(keyword.toLowerCase()))
			.count();
		return matchCount >= 1; // 최소 1개 키워드 포함
	}

	// 최소한의 부동산 관련성 검사 (완화된 조건용)
	private boolean hasMinimalRealEstateRelevance(String text) {
		List<String> basicKeywords = Arrays.asList("부동산", "주택", "아파트", "청약", "분양", "임대", "전세");
		return basicKeywords.stream()
			.anyMatch(keyword -> text.contains(keyword.toLowerCase()));
	}

	// 광고 점수 계산
	private int calculateAdScore(String text) {
		int score = 0;
		for (String keyword : WEAK_AD_KEYWORDS) {
			if (text.contains(keyword.toLowerCase())) {
				score += 1;
			}
		}
		return score;
	}

	// 관련성 점수 계산
	private int calculateRelevanceScore(String text) {
		int score = 0;
		for (String keyword : REAL_ESTATE_KEYWORDS) {
			if (text.contains(keyword.toLowerCase())) {
				score += 2; // 부동산 키워드는 높은 점수
			}
		}
		return score;
	}

	// 기존 메서드들은 동일하게 유지...
	private List<NewsItem> fetchNewsFromNaver(String query) {
		try {
			String clientId = getNaverClientId().trim();
			String clientSecret = getNaverClientSecret().trim();
			if (clientId.isEmpty() || clientSecret.isEmpty()) {
				log.error("네이버 API 클라이언트 ID 또는 시크릿이 설정되지 않았습니다.");
				return List.of();
			}

			String encodedQuery = java.net.URLEncoder.encode(query, StandardCharsets.UTF_8);
			String uri = String.format("?query=%s&display=%d&start=1&sort=date",
				encodedQuery, newsConfig.getApi().getMaxResults());

			String responseBody = webClient.get()
				.uri(uri)
				.header("X-Naver-Client-Id", clientId)
				.header("X-Naver-Client-Secret", clientSecret)
				.retrieve()
				.onStatus(status -> !status.is2xxSuccessful(), clientResponse ->
					clientResponse.bodyToMono(String.class).flatMap(body -> {
						log.error("네이버 뉴스 API 응답 오류: {}", body);
						return Mono.error(new RuntimeException("Naver API Error: " + body));
					})
				)
				.bodyToMono(String.class)
				.block();

			log.debug("네이버 API 응답 길이: {}", responseBody != null ? responseBody.length() : 0);
			return parseNaverNewsResponse(responseBody);

		} catch (Exception e) {
			log.error("네이버 뉴스 API 호출 실패 - 쿼리: {}", query, e);
			return List.of();
		}
	}

	private List<NewsItem> parseNaverNewsResponse(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode items = root.path("items");
			return objectMapper.convertValue(
				items,
				objectMapper.getTypeFactory().constructCollectionType(List.class, NewsItem.class)
			);
		} catch (Exception e) {
			log.error("뉴스 응답 파싱 실패", e);
			return List.of();
		}
	}

	private String extractDomain(String url) {
		try {
			URI uri = new URI(url);
			String host = uri.getHost();
			if (host == null)
				return "";
			return host.startsWith("www.") ? host.substring(4) : host;
		} catch (Exception e) {
			return "";
		}
	}

	private void createDailySummaryPost(List<NewsItem> newsItems) {
		String summaryContent = buildDailySummaryContent(newsItems);
		String title = String.format("[일간 부동산 뉴스] %s - %d개 기사 요약",
			LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")),
			newsItems.size());

		UserDetailsImpl currentUser = (UserDetailsImpl)SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal();

		Post summaryPost = Post.builder()
			.username(currentUser.getUser().getUsername())
			.title(title)
			.content(summaryContent)
			.category(PostCategory.INFO_SHARE)
			.likes(0)
			.views(0L)
			.build();

		postRepository.save(summaryPost);
	}

	private boolean isDailySummaryAlreadyCreated() {
		LocalDate today = LocalDate.now();
		UserDetailsImpl currentUser = (UserDetailsImpl)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		return postRepository.findByTitleContainingAndUsernameAndDeletedAtIsNull(
				"[일간 부동산 뉴스]", currentUser.getUser().getUsername())
			.stream()
			.anyMatch(post -> post.getCreatedAt().toLocalDate().equals(today));
	}

	private String buildDailySummaryContent(List<NewsItem> newsItems) {
		StringBuilder content = new StringBuilder();
		content.append("📰 **오늘의 부동산 뉴스 요약**\n\n");
		content.append("📅 ")
			.append(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
			.append(" 기준\n\n");
		content.append("---\n\n");

		// 카테고리별 분류
		List<NewsItem> policyNews = new ArrayList<>();
		List<NewsItem> subscriptionNews = new ArrayList<>();
		List<NewsItem> otherNews = new ArrayList<>();

		for (NewsItem item : newsItems) {
			String title = cleanHtmlTags(item.title).toLowerCase();
			if (title.contains("정책") || title.contains("규제") || title.contains("법")) {
				policyNews.add(item);
			} else if (title.contains("청약") || title.contains("분양") || title.contains("공급")) {
				subscriptionNews.add(item);
			} else {
				otherNews.add(item);
			}
		}

		// 정책 뉴스
		if (!policyNews.isEmpty()) {
			content.append("## 🏛️ 부동산 정책 동향\n\n");
			appendNewsSection(content, policyNews);
		}

		// 청약/분양 뉴스
		if (!subscriptionNews.isEmpty()) {
			content.append("## 🏠 청약/분양 정보\n\n");
			appendNewsSection(content, subscriptionNews);
		}

		// 기타 뉴스
		if (!otherNews.isEmpty()) {
			content.append("## 📈 기타 부동산 소식\n\n");
			appendNewsSection(content, otherNews);
		}

		content.append("\n---\n");
		content.append("*이 요약은 시스템에 의해 자동으로 생성되었습니다. ");
		content.append("자세한 내용은 각 기사의 원문을 확인해 주세요.*\n\n");
		content.append("**📊 오늘의 뉴스 수집 현황**\n");
		content.append("- 총 수집 기사 수: ").append(newsItems.size()).append("개\n");
		content.append("- 정책 관련: ").append(policyNews.size()).append("개\n");
		content.append("- 청약/분양: ").append(subscriptionNews.size()).append("개\n");
		content.append("- 기타: ").append(otherNews.size()).append("개\n");

		return content.toString();
	}

	private void appendNewsSection(StringBuilder content, List<NewsItem> newsItems) {
		for (int i = 0; i < newsItems.size(); i++) {
			NewsItem item = newsItems.get(i);
			content.append("**").append(i + 1).append(". ").append(cleanHtmlTags(item.title)).append("**\n");
			content.append("📝 ").append(cleanHtmlTags(item.description)).append("\n");
			content.append("🔗 [기사 보기](")
				.append(item.originallink != null ? item.originallink : item.link)
				.append(")\n");
			content.append("📅 ").append(formatPublishDate(item.pubDate)).append("\n\n");
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

	private String getNaverClientId() {
		// Try config first
		String apiKey = newsConfig.getApi().getKey();
		log.debug("API Key from config: {}", apiKey);

		if (apiKey != null && apiKey.contains(":") && !apiKey.contains("${")) {
			String clientId = apiKey.split(":")[0];
			log.debug("Using Client ID from config: {}", clientId);
			return clientId;
		}

		// Fallback to environment variable (original approach)
		String envClientId = System.getenv("NAVER_CLIENT_ID");
		log.debug("Fallback to env NAVER_CLIENT_ID: {}", envClientId);
		return envClientId;
	}

	private String getNaverClientSecret() {
		// Try config first
		String apiKey = newsConfig.getApi().getKey();

		if (apiKey != null && apiKey.contains(":") && !apiKey.contains("${")) {
			String clientSecret = apiKey.split(":")[1];
			log.debug("Using Client Secret from config");
			return clientSecret;
		}

		// Fallback to environment variable (original approach)
		String envClientSecret = System.getenv("NAVER_CLIENT_SECRET");
		log.debug("Fallback to env NAVER_CLIENT_SECRET: {}", envClientSecret != null ? "***" : "null");
		return envClientSecret;
	}

	public static class NewsItem {
		public String title;
		public String originallink;
		public String link;
		public String description;
		public String pubDate;
	}
}