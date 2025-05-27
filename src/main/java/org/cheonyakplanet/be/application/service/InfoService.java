package org.cheonyakplanet.be.application.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cheonyakplanet.be.application.dto.RealEstatePriceSummaryDTO;
import org.cheonyakplanet.be.domain.entity.RealEstatePrice;
import org.cheonyakplanet.be.domain.entity.subscription.SggCode;
import org.cheonyakplanet.be.domain.repository.RealEstatePriceRepository;
import org.cheonyakplanet.be.domain.repository.RealEstatePriceSummaryRepository;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.domain.util.Utils;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoService {

	private final SggCodeRepository sggCodeRepository;
	private final RealEstatePriceRepository priceRepository;
	private final RealEstatePriceSummaryRepository priceSummaryRepository;

	private final Utils utils;

	@Qualifier("taskExecutor")
	private final TaskExecutor taskExecutor;

	@Value("${public.api.key}")
	private String apiKey;

	@Value("${realestate.api.url}")
	private String priceUrl;

	public List<String> getReginList() {
		List<String> result = sggCodeRepository.findAllRegions();

		if (result.isEmpty()) {
			throw new CustomException(ErrorCode.INFO005, "지역 테이블 없음 DB 확인");
		}
		return result;
	}

	public List<String> getCityList(String region) {
		List<String> result = sggCodeRepository.findAllCities(region);

		if (result.isEmpty()) {
			throw new CustomException(ErrorCode.INFO005, "지역 테이블 없음 DB 확인");
		}
		return result;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void collectRealPrice(String yyyyMM) {
		String callDate = yyyyMM;
		if (callDate == null || callDate.isEmpty()) {
			callDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
		}

		try {
			// 비동기 작업 실행 및 완료 대기
			List<CompletableFuture<Void>> futures = ingestAll(callDate);

			// 모든 비동기 작업이 완료될 때까지 대기
			CompletableFuture<Void> allFutures = CompletableFuture.allOf(
				futures.toArray(new CompletableFuture[0])
			);

			// 완료될 때까지 대기
			allFutures.get(30, TimeUnit.MINUTES); // 최대 30분 대기

			log.info("모든 데이터 수집 작업이 완료되었습니다. 이제 요약 작업을 수행합니다.");

			// 요약 작업 수행
			refreshSummary();
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			log.error("데이터 수집 작업 대기 중 오류 발생: {}", e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CompletableFuture<Void>> ingestAll(String callDate) {
		List<SggCode> codes = sggCodeRepository.findAll();
		List<CompletableFuture<Void>> futures = new ArrayList<>();

		for (SggCode code : codes) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					ingestOne(code, callDate);
				} catch (Exception e) {
					log.error("Ingest failed for {}: {}", code.getSggCd5(), e.getMessage(), e);
				}
			}, taskExecutor);

			futures.add(future);
		}

		return futures;
	}

	@Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void ingestOne(SggCode code, String callDate) throws Exception {
		try {
			String url = String.format("%s?serviceKey=%s&LAWD_CD=%s&DEAL_YMD=%s&numOfRows=1000", priceUrl, apiKey,
				code.getSggCd5(), callDate);
			HttpURLConnection conn = null;
			StringBuilder sb = new StringBuilder();
			try {
				URL apiUrl = new URL(url);
				conn = (HttpURLConnection)apiUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(30000);
				conn.setReadTimeout(60000);
				int status = conn.getResponseCode();
				BufferedReader reader = new BufferedReader(
					new InputStreamReader(status == 200 ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"));
				String line;
				while ((line = reader.readLine()) != null)
					sb.append(line);
				reader.close();
			} finally {
				if (conn != null)
					conn.disconnect();
			}
			String response = sb.toString();
			//log.info("API 응답 (처음 500자): {}", response.length() > 1000 ? response.substring(0, 1000) + "..." : response);
			List<RealEstatePrice> list = parseResponse(response, code);
			if (list.isEmpty())
				throw new RuntimeException("No items parsed for " + code.getSggCd5());
			priceRepository.saveAll(list);
			log.info("Saved {} records for {}", list.size(), code.getSggCd5());
		} catch (Exception e) {
			log.error("데이터 로드 에러 : {}", e);
		}
	}

	private List<RealEstatePrice> parseResponse(String xml, SggCode code) throws Exception {
		List<RealEstatePrice> result = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));

			NodeList items = doc.getElementsByTagName("item");
			log.info("발견된 item 요소 수: {}", items.getLength());

			for (int i = 0; i < items.getLength(); i++) {
				Node item = items.item(i);
				RealEstatePrice p = new RealEstatePrice();

				// 기본 정보 설정
				p.setRegion(code.getSggCdNmRegion());
				p.setSggCdNm(code.getSggCdNmCity());
				p.setSggCd(String.valueOf(code.getSggCd5()));

				// 필수 데이터 추출 - null 체크 추가
				String district = getNodeValue(item, "umdNm");
				if (district != null) {
					p.setUmdNm(district);
				} else {
					log.warn("항목 #{}: 법정동 정보가 누락되었습니다.", i);
				}

				p.setAptDong(getNodeValue(item, "aptDong"));
				p.setAptNm(getNodeValue(item, "aptNm"));

				// 년/월/일 처리 - null 체크 필수
				Integer dealYear = utils.getInt(item, "dealYear");
				Integer dealMonth = utils.getInt(item, "dealMonth");
				Integer dealDay = utils.getInt(item, "dealDay");

				// 필수 데이터가 없으면 건너뛰기
				if (dealYear == null || dealMonth == null || dealDay == null) {
					log.warn("항목 #{}: 거래일자 정보가 누락되었습니다. year={}, month={}, day={}", i, dealYear, dealMonth, dealDay);
					continue;
				}

				p.setDealYear(dealYear);
				p.setDealMonth(dealMonth);
				p.setDealDay(dealDay);

				// 거래금액 처리
				String amt = getNodeValue(item, "dealAmount");
				if (amt != null && !amt.trim().isEmpty()) {
					try {
						p.setDealAmount(Long.parseLong(amt.replace(",", "")));
					} catch (NumberFormatException e) {
						log.warn("항목 #{}: 거래금액 변환 실패: {}", i, amt);
					}
				}

				// 날짜 형식 처리 - null 체크 포함된 formatDate 메서드 사용
				Date date = utils.formatDate(dealYear, dealMonth, dealDay);
				if (date != null) {
					p.setDealDate(date);
				}

				// 전용면적 처리
				p.setExcluUseAr(utils.getBigDecimal(item, "excluUseAr"));

				// 모든 필수 필드가 설정되었는지 확인
				if (p.getDealYear() != null && p.getDealMonth() != null && p.getDealDay() != null) {
					result.add(p);
				} else {
					log.warn("항목 #{}: 필수 필드가 누락되어 저장하지 않습니다.", i);
				}
			}

			log.info("총 {}개의 부동산 가격 정보가 파싱되었습니다.", result.size());

		} catch (Exception e) {
			log.error("XML 파싱 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}

		return result;
	}

	private String getNodeValue(Node node, String tag) {
		NodeList nl = ((org.w3c.dom.Element)node).getElementsByTagName(tag);
		if (nl.getLength() > 0)
			return nl.item(0).getTextContent().trim();
		return null;
	}

	public List<RealEstatePriceSummaryDTO> getRealEstateSummaryDong(String region, String city, String umdNm) {

		List<Object[]> results = priceSummaryRepository.findByRegionAndSggCdNmAndUmdNm(region, city, umdNm);

		return results.stream()
			.map(row -> RealEstatePriceSummaryDTO.builder()
				//.region((String)row[0])
				//.sggCdNm((String)row[1])
				//.umdNm((String)row[2])
				.dealYearMonth(((Integer)row[3]) * 100 + ((Integer)row[4]))
				// .dealMonth((Integer)row[4])
				.dealCount((Integer)row[5])
				.pricePerAr((Long)row[6])
				.build())
			.collect(Collectors.toList());
	}

	public List<RealEstatePriceSummaryDTO> getRealEstateSummaryGu(String region, String city) {

		List<Object[]> results = priceSummaryRepository.findByRegionAndSggCdNm(region, city);

		return results.stream()
			.map(row -> RealEstatePriceSummaryDTO.builder()
				.dealYearMonth(((Number)row[2]).intValue() * 100 + ((Number)row[3]).intValue())
				.dealCount(((BigDecimal)row[4]).intValue())
				.pricePerAr(((BigDecimal)row[5]).longValue())
				.build())
			.collect(Collectors.toList());
	}

	@Transactional
	public void refreshSummary() {
		priceSummaryRepository.deleteAll();
		priceSummaryRepository.insertSummary(); //TODO : 생성마다 모든 데이터 요약되는거 수정!!
	}
}
