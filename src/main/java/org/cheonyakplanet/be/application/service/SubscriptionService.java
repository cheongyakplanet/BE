package org.cheonyakplanet.be.application.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.application.dto.subscription.CoordinateResponseDTO;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLikeRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLocationInfoRepository;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

	private final SubscriptionInfoRepository subscriptionInfoRepository;
	private final SubscriptionLocationInfoRepository subscriptionLocationInfoRepository;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final SubscriptionLikeRepository subscriptionLikeRepository;

	@Value("${public.api.key}")
	private String apiKey;

	@Value("${sub.apt.api.url}")
	private String subAptApiUrl;

	@Value("${sub.apt2.api.url}")
	private String subApt2ApiUrl;

	@Value("${sub.other.api.url}")
	private String subOtherApiUrl;

	@Value("${kakao.rest.api.key}")
	private String kakaoRestApiKey;

	@Value("${kakao.latitude.url}")
	private String kakaoLatitudeUrl;

	public String updateSubAPT() {
		String requestUrl = subAptApiUrl + "?page=1&perPage=50&" + "serviceKey=" + apiKey;

		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder response = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			log.info("API url: {}", url);
			log.info("API Response: {}", response.toString());

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response.toString());
			JsonNode items = rootNode.path("data");

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Set<String> existingHouseManageNos = subscriptionInfoRepository.findAllHouseManageNos();

			for (JsonNode item : items) {

				String houseManageNo = item.path("HOUSE_MANAGE_NO").asText();

				// 이미 존재하면 skip
				if (existingHouseManageNos.contains(houseManageNo))
					continue;

				SubscriptionInfo subscriptionInfo = new SubscriptionInfo();

				String hssplyAdres = item.path("HSSPLY_ADRES").asText();
				String[] addressParts = parseAddress(hssplyAdres);

				if (addressParts != null) {
					subscriptionInfo.setRegion(addressParts[0]); // 예: "울산광역시"
					subscriptionInfo.setCity(addressParts[1]);   // 예: "중구"
					subscriptionInfo.setDistrict(addressParts[2]); // 예: "우정동"
					subscriptionInfo.setDetail(addressParts[3]);   // 예: "286-1번지"
				}
				subscriptionInfo.setBsnsMbyNm(item.path("BSNS_MBY_NM").asText()); // 사업주체명
				subscriptionInfo.setCnstrctEntrpsNm(item.path("CNSTRCT_ENTRPS_NM").asText()); // 건설업체명

				subscriptionInfo.setHouseNm(item.path("HOUSE_NM").asText()); // 주택명
				subscriptionInfo.setHouseManageNo(item.path("HOUSE_MANAGE_NO").asText()); // 주택관리번호
				subscriptionInfo.setHouseDtlSecd(item.path("HOUSE_DTL_SECD").asText()); // 주택상세구분코드
				subscriptionInfo.setHouseDtlSecdNm(item.path("HOUSE_DTL_SECD_NM").asText()); // 주택상세구분코드명
				subscriptionInfo.setRentSecd(item.path("RENT_SECD").asText()); // 분양구분코드
				subscriptionInfo.setRentSecdNm(item.path("RENT_SECD_NM").asText()); // 분양구분코드명

				subscriptionInfo.setSubscrptAreaCode(item.path("SUBSCRPT_AREA_CODE").asText()); // 공급지역코드
				subscriptionInfo.setSubscrptAreaCodeNm(item.path("SUBSCRPT_AREA_CODE_NM").asText()); // 공급지역명
				subscriptionInfo.setHssplyAdres(hssplyAdres); // 공급위치
				subscriptionInfo.setHssplyZip(item.path("HSSPLY_ZIP").asText()); // 공급위치 우편번호

				subscriptionInfo.setRceptBgnde(parseDate(item.path("RCEPT_BGNDE").asText(), dateFormat)); // 청약 접수 시작일
				subscriptionInfo.setRceptEndde(parseDate(item.path("RCEPT_ENDDE").asText(), dateFormat)); // 청약 접수 종료일
				subscriptionInfo.setSpsplyRceptBgnde(
					parseDate(item.path("SPSPLY_RCEPT_BGNDE").asText(), dateFormat)); // 특별공급 접수 시작일
				subscriptionInfo.setSpsplyRceptEndde(
					parseDate(item.path("SPSPLY_RCEPT_ENDDE").asText(), dateFormat)); // 특별공급 접수 종료일

				subscriptionInfo.setGnrlRnk1CrspareaRcptde(
					parseDate(item.path("GNRL_RNK1_CRSPAREA_RCPTDE").asText(), dateFormat)); // 1순위 해당지역 접수 시작일
				subscriptionInfo.setGnrlRnk1CrspareaEndde(
					parseDate(item.path("GNRL_RNK1_CRSPAREA_ENDDE").asText(), dateFormat)); // 1순위 해당지역 접수 종료일

				subscriptionInfo.setGnrlRnk1EtcAreaRcptde(
					parseDate(item.path("GNRL_RNK1_ETC_AREA_RCPTDE").asText(), dateFormat)); // 1순위 기타지역 접수 시작일
				subscriptionInfo.setGnrlRnk1EtcAreaEndde(
					parseDate(item.path("GNRL_RNK1_ETC_AREA_ENDDE").asText(), dateFormat)); // 1순위 기타지역 접수 종료일

				subscriptionInfo.setPrzwnerPresnatnDe(
					parseDate(item.path("PRZWNER_PRESNATN_DE").asText(), dateFormat)); // 당첨자 발표일
				subscriptionInfo.setCntrctCnclsBgnde(
					parseDate(item.path("CNTRCT_CNCLS_BGNDE").asText(), dateFormat)); // 계약 시작일
				subscriptionInfo.setCntrctCnclsEndde(
					parseDate(item.path("CNTRCT_CNCLS_ENDDE").asText(), dateFormat)); // 계약 종료일

				subscriptionInfo.setPblancUrl(item.path("PBLANC_URL").asText()); // 모집공고 상세 URL

				subscriptionInfoRepository.save(subscriptionInfo);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "API 불러오기 및 DB저장 성공";
	}

	private LocalDate parseDate(String dateStr, SimpleDateFormat dateFormat) {
		try {
			Date date = dateFormat.parse(dateStr);
			// Date를 Instant로 변환하고 LocalDate로 변환
			return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (Exception e) {
			log.warn("날짜 파싱 실패: {}", dateStr, e);
			return null;
		}
	}

	private String[] parseAddress(String address) {
		// 시도 (특별시, 광역시, 도) + (구/군/시) + (동/가/도로명 등) + 나머지 주소를 파싱
		Pattern pattern = Pattern.compile(
			"^(\\S+시|\\S+도|\\S+특별자치시)\\s?" + // 시도: 서울특별시, 경기도, 세종특별자치시 등
				"(\\S+구|\\S+군|\\S+시)?\\s?" +          // 시군구: 영등포구, 아산시 등 (선택적)
				"((?:\\S+동(?:\\d*가)?|\\S+읍|\\S+면|.+로|.+길)\\s?(?:\\d+번지)?)?\\s?" + // 읍면동/도로명, 번지 포함
				"(.+)?$"                                // 나머지 주소
		);
		Matcher matcher = pattern.matcher(address);

		if (matcher.find()) {
			// 각 그룹을 확인하며 null인 경우를 빈 문자열로 처리
			String region1 = matcher.group(1) != null ? matcher.group(1) : ""; // 시도
			String region2 = matcher.group(2) != null ? matcher.group(2) : ""; // 시군구
			String region3 = matcher.group(3) != null ? matcher.group(3) : ""; // 읍면동/도로명
			String restAddress = matcher.group(4) != null ? matcher.group(4) : ""; // 나머지 주소

			return new String[] {region1, region2, region3, restAddress};
		}
		return null; // 주소 형식이 맞지 않으면 null 반환
	}

	//    subscriptionLocationInfoRepository
	public List<CoordinateResponseDTO> updateAllSubscriptionCoordinates() {
		List<SubscriptionInfo> subscriptions = subscriptionInfoRepository.findAll();
		List<CoordinateResponseDTO> coordinateResponses = new ArrayList<>();
		List<SubscriptionLocationInfo> locationInfos = new ArrayList<>();

		Set<Long> existingIds = subscriptionLocationInfoRepository.findAll()
			.stream()
			.map(SubscriptionLocationInfo::getId)
			.collect(Collectors.toSet());

		// WebClient는 반복문 외부에서 한 번 생성해 재사용
		WebClient webClient = WebClient.builder()
			.baseUrl("https://dapi.kakao.com")
			.defaultHeader("Authorization", "KakaoAK " + kakaoRestApiKey)
			.build();

		for (SubscriptionInfo subscription : subscriptions) {
			Long id = subscription.getId();

			// 이미 위치정보가 있으면 스킵
			if (existingIds.contains(id)) {
				continue;
			}

			String addr = subscription.getHssplyAdres();
			try {
				Mono<String> responseMono = webClient.get()
					.uri(uriBuilder -> uriBuilder
						.path("/v2/local/search/address.json")
						.queryParam("query", addr)
						.build()
					)
					.retrieve()
					.bodyToMono(String.class);

				String responseBody = responseMono.block();
				if (responseBody != null) {
					JSONObject jsonObject = new JSONObject(responseBody);
					JSONArray documents = jsonObject.getJSONArray("documents");

					if (documents.length() > 0) {
						JSONObject firstDoc = documents.getJSONObject(0);
						String x = firstDoc.getString("x"); // 경도
						String y = firstDoc.getString("y"); // 위도

						// 새로운 SubscriptionLocationInfo 생성 (id, 위도, 경도)
						SubscriptionLocationInfo locationInfo = SubscriptionLocationInfo.builder()
							.id(subscription.getId())
							.latitude(y)
							.longitude(x)
							.build();
						locationInfos.add(locationInfo);

						coordinateResponses.add(new CoordinateResponseDTO(x, y));
					} else {
						coordinateResponses.add(new CoordinateResponseDTO("NotFound", "NotFound"));
					}
				} else {
					coordinateResponses.add(new CoordinateResponseDTO("NoResponse", "NoResponse"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				coordinateResponses.add(new CoordinateResponseDTO("Error", "Error"));
			}
		}
		subscriptionLocationInfoRepository.saveAll(locationInfos);

		return coordinateResponses;
	}

	public Object getPopularLocationList() {
		List<String> popularLocal = userRepository.findInterestLocal1TopByInterestLocal1(PageRequest.of(0, 5));
		popularLocal.removeIf(Objects::isNull);
		return popularLocal;
	}

	/**
	 * 내 관심 지역 리턴
	 *
	 * @param
	 * @return
	 */
	public List<String> getInterestLocalsByEmail(HttpServletRequest request) {
		String token = jwtUtil.getTokenFromRequest(request);
		String email = jwtUtil.getUserInfoFromToken(token).getSubject();
		log.info("Extracted email: {}", email);

		// 이메일 기반 관심 지역 조회
		List<Object[]> rawInterestLocals = userRepository.myInterestLocals(email);

		if (rawInterestLocals.isEmpty()) {
			return new ArrayList<>(Integer.parseInt("로그인이 필요합니다!"));
		}

		// Object[] 데이터를 List<String>으로 변환
		List<String> interestLocals = new ArrayList<>();
		for (Object[] locals : rawInterestLocals) {
			for (Object local : locals) {
				if (local != null) { // null 값 필터링
					interestLocals.add(local.toString());
				}
			}
		}

		log.info("Processed interest locals: {}", interestLocals);
		return interestLocals;
	}

	public long getPopularSubId() {
		return subscriptionLikeRepository.findTopLikedSubscriptionId();
	}

}
