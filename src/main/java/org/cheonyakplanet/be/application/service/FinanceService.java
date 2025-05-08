package org.cheonyakplanet.be.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.cheonyakplanet.be.domain.entity.finance.HouseLoan;
import org.cheonyakplanet.be.domain.entity.finance.Mortgage;
import org.cheonyakplanet.be.domain.repository.HouseLoanRepository;
import org.cheonyakplanet.be.domain.repository.MortgageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

	private final RestTemplate restTemplate;

	private final MortgageRepository mortgageRepository;
	private final HouseLoanRepository houseLoanRepository;

	@Value("${fss.api.key}")
	private String fssApiKey;

	@Value("${mortgageLoan.api.url}")
	private String mortgageLoanApiUrl;

	@Value("${rentHouseLoan.api.url}")
	private String rentHouseLoanApiUrl;

	public String updateMortgageLoan() {
		try {
			String requestUrl = mortgageLoanApiUrl + "?auth=" + fssApiKey + "&topFinGrpNo=050000&pageNo=1";
			log.info("requestUrl : {}", requestUrl);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET,
				new HttpEntity<>(headers), String.class);

			String responseBody = response.getBody();
			log.info("API Response: {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(responseBody);
			JsonNode result = rootNode.path("result");
			JsonNode baseList = result.path("baseList");
			JsonNode optionList = result.path("optionList");

			for (JsonNode baseItem : baseList) {

				String finPrdtCd = baseItem.path("fin_prdt_cd").asText();

				for (JsonNode optionItem : optionList) {
					if (finPrdtCd.equals(optionItem.path("fin_prdt_cd").asText())) {
						Mortgage mortgage = new Mortgage();

						mortgage.setFinCoNo(finPrdtCd);
						mortgage.setDclsMonth(baseItem.path("dcls_month").asText());
						mortgage.setFinPrdtCd(baseItem.path("fin_prdt_cd").asText());
						mortgage.setKorCoNm(baseItem.path("kor_co_nm").asText());
						mortgage.setFinPrdtNm(baseItem.path("fin_prdt_nm").asText());
						mortgage.setJoinWay(baseItem.path("join_way").asText());
						mortgage.setLoanInciExpn(baseItem.path("loan_inci_expn").asText());
						mortgage.setErlyRpayFee(baseItem.path("erly_rpay_fee").asText());
						mortgage.setDlyRate(baseItem.path("dly_rate").asText());
						mortgage.setLoanLmt(baseItem.path("loan_lmt").asText());
						mortgage.setDclsStrtDay(baseItem.path("dcls_strt_day").asText());
						mortgage.setDclsEndDay(baseItem.path("dcls_end_day").asText());
						mortgage.setFinCoSubmDay(baseItem.path("fin_co_subm_day").asText());

						mortgage.setMrtgType(optionItem.path("mrtg_type").asText());
						mortgage.setMrtgTypeNm(optionItem.path("mrtg_type_nm").asText());
						mortgage.setRpayType(optionItem.path("rpay_type").asText());
						mortgage.setRpayTypeNm(optionItem.path("rpay_type_nm").asText());
						mortgage.setLendRateType(optionItem.path("lend_rate_type").asText());
						mortgage.setLendRateTypeNm(optionItem.path("lend_rate_type_nm").asText());
						mortgage.setLendRateMin(optionItem.path("lend_rate_min").asDouble());
						mortgage.setLendRateMax(optionItem.path("lend_rate_max").asDouble());
						mortgage.setLendRateAvg(optionItem.path("lend_rate_avg").asDouble());

						mortgageRepository.save(mortgage);
					}
				}
			}

		} catch (Exception e) {
			log.error("Error updating deposit data", e);
			return "API 데이터 처리 중 오류 발생: " + e.getMessage();
		}
		return "API 불러오기 및 DB저장 성공";
	}

	public String updateRenthouse() {
		try {
			String requestUrl = rentHouseLoanApiUrl + "?auth=" + fssApiKey + "&topFinGrpNo=050000&pageNo=1";
			log.info("requestUrl : {}", requestUrl);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET,
				new HttpEntity<>(headers), String.class);

			String responseBody = response.getBody();
			log.info("API Response: {}", responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(responseBody);
			JsonNode result = rootNode.path("result");
			JsonNode baseList = result.path("baseList");
			JsonNode optionList = result.path("optionList");

			for (JsonNode baseItem : baseList) {
				String finPrdtCd = baseItem.path("fin_prdt_cd").asText();

				for (JsonNode optionItem : optionList) {
					if (finPrdtCd.equals(optionItem.path("fin_prdt_cd").asText())) {
						HouseLoan houseLoan = new HouseLoan();

						houseLoan.setFinCoNo(finPrdtCd);
						houseLoan.setDclsMonth(baseItem.path("dcls_month").asText());
						houseLoan.setFinPrdtCd(baseItem.path("fin_prdt_cd").asText());
						houseLoan.setKorCoNm(baseItem.path("kor_co_nm").asText());
						houseLoan.setFinPrdtNm(baseItem.path("fin_prdt_nm").asText());
						houseLoan.setJoinWay(baseItem.path("join_way").asText());
						houseLoan.setLoanInciExpn(baseItem.path("loan_inci_expn").asText());
						houseLoan.setErlyRpayFee(baseItem.path("erly_rpay_fee").asText());
						houseLoan.setDlyRate(baseItem.path("dly_rate").asText());
						houseLoan.setLoanLmt(baseItem.path("loan_lmt").asText());
						houseLoan.setDclsStrtDay(baseItem.path("dcls_strt_day").asText());
						houseLoan.setDclsEndDay(baseItem.path("dcls_end_day").asText());
						houseLoan.setFinCoSubmDay(baseItem.path("fin_co_subm_day").asText());

						houseLoan.setRpayType(optionItem.path("rpay_type").asText());
						houseLoan.setRpayTypeNm(optionItem.path("rpay_type_nm").asText());
						houseLoan.setLendRateType(optionItem.path("lend_rate_type").asText());
						houseLoan.setLendRateTypeNm(optionItem.path("lend_rate_type_nm").asText());
						houseLoan.setLendRateMin(optionItem.path("lend_rate_min").asDouble());
						houseLoan.setLendRateMax(optionItem.path("lend_rate_max").asDouble());
						houseLoan.setLendRateAvg(optionItem.path("lend_rate_avg").asDouble());

						houseLoanRepository.save(houseLoan);

					}
				}
			}
			return "API 불러오기 및 DB저장 성공";

		} catch (Exception e) {
			log.error("Error updating deposit data", e);
			return "API 데이터 처리 중 오류 발생: " + e.getMessage();
		}
	}
}
