package org.cheonyakplanet.be.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.cheonyakplanet.be.domain.entity.finance.HouseLoan;
import org.cheonyakplanet.be.domain.entity.finance.Mortgage;
import org.cheonyakplanet.be.domain.repository.HouseLoanRepository;
import org.cheonyakplanet.be.domain.repository.MortgageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("금융 서비스 테스트")
class FinanceServiceTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private MortgageRepository mortgageRepository;

	@Mock
	private HouseLoanRepository houseLoanRepository;

	@InjectMocks
	private FinanceService financeService;

	private Mortgage testMortgage;
	private HouseLoan testHouseLoan;
	private String testApiKey;
	private String testMortgageApiUrl;
	private String testHouseLoanApiUrl;

	@BeforeEach
	void setUp() {
		// Given: 테스트용 데이터 설정
		testApiKey = "test-api-key";
		testMortgageApiUrl = "https://test-mortgage-api.com";
		testHouseLoanApiUrl = "https://test-houseloan-api.com";

		ReflectionTestUtils.setField(financeService, "fssApiKey", testApiKey);
		ReflectionTestUtils.setField(financeService, "mortgageLoanApiUrl", testMortgageApiUrl);
		ReflectionTestUtils.setField(financeService, "rentHouseLoanApiUrl", testHouseLoanApiUrl);

		testMortgage = Mortgage.builder()
			.finCoNo("0010001")
			.finPrdtCd("10110001001")
			.korCoNm("테스트은행")
			.finPrdtNm("테스트주택담보대출")
			.lendRateType("변동금리")
			.lendRateMin(3.5)
			.lendRateMax(5.5)
			.build();

		testHouseLoan = HouseLoan.builder()
			.finCoNo("0010002")
			.finPrdtCd("10110002001")
			.korCoNm("테스트저축은행")
			.finPrdtNm("테스트전세자금대출")
			.lendRateType("고정금리")
			.lendRateMin(2.8)
			.lendRateMax(4.2)
			.build();
	}

	// @Test
	// @DisplayName("주택담보대출 정보 업데이트 성공 테스트")
	// void givenValidApiResponse_whenUpdateMortgageLoan_thenReturnSuccessMessage() {
	// 	// Given: 외부 API에서 정상 응답을 반환하는 시나리오
	// 	String mockApiResponse = """
	// 		{
	// 		    "result": {
	// 		        "baseList": [
	// 		            {
	// 		                "fin_co_no": "0010001",
	// 		                "fin_prdt_cd": "10110001001",
	// 		                "kor_co_nm": "테스트은행",
	// 		                "fin_prdt_nm": "테스트주택담보대출",
	// 		                "lend_rate_type": "변동금리",
	// 		                "lend_rate_min": 3.5,
	// 		                "lend_rate_max": 5.5
	// 		            }
	// 		        ]
	// 		    }
	// 		}
	// 		""";
	//
	// 	given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
	// 		.willReturn(ResponseEntity.ok(mockApiResponse));
	// 	given(mortgageRepository.saveAll(anyList())).willReturn(Arrays.asList(testMortgage));
	//
	// 	// When: 주택담보대출 정보 업데이트 메서드 호출
	// 	String result = financeService.updateMortgageLoan();
	//
	// 	// Then: 성공 메시지가 반환되고 저장소에 데이터가 저장됨
	// 	assertThat(result).contains("주택담보대출 정보가 업데이트되었습니다");
	// 	then(restTemplate).should().exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
	// 	then(mortgageRepository).should().saveAll(anyList());
	// }

	@Test
	@DisplayName("주택담보대출 정보 업데이트 실패 테스트 - API 호출 오류")
	void givenApiCallFails_whenUpdateMortgageLoan_thenReturnErrorMessage() {
		// Given: 외부 API 호출 시 예외가 발생하는 시나리오
		given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
			.willThrow(new RestClientException("API 연결 실패"));

		// When: 주택담보대출 정보 업데이트 메서드 호출
		String result = financeService.updateMortgageLoan();

		// Then: 오류 메시지가 반환되고 저장소 호출이 발생하지 않음
		assertThat(result).contains("API 데이터 처리 중 오류 발생");
		then(mortgageRepository).should(never()).saveAll(anyList());
	}

	// @Test
	// @DisplayName("전세자금대출 정보 업데이트 성공 테스트")
	// void givenValidApiResponse_whenUpdateRentHouseLoan_thenReturnSuccessMessage() {
	// 	// Given: 전세자금대출 API에서 정상 응답을 반환하는 시나리오
	// 	String mockApiResponse = """
	// 		{
	// 		    "result": {
	// 		        "baseList": [
	// 		            {
	// 		                "fin_co_no": "0010002",
	// 		                "fin_prdt_cd": "10110002001",
	// 		                "kor_co_nm": "테스트저축은행",
	// 		                "fin_prdt_nm": "테스트전세자금대출",
	// 		                "lend_rate_type": "고정금리",
	// 		                "lend_rate_min": 2.8,
	// 		                "lend_rate_max": 4.2
	// 		            }
	// 		        ]
	// 		    }
	// 		}
	// 		""";
	//
	// 	given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
	// 		.willReturn(ResponseEntity.ok(mockApiResponse));
	// 	given(houseLoanRepository.saveAll(anyList())).willReturn(Arrays.asList(testHouseLoan));
	//
	// 	// When: 전세자금대출 정보 업데이트 메서드 호출
	// 	String result = financeService.updateRentHouseLoan();
	//
	// 	// Then: 성공 메시지가 반환되고 저장소에 데이터가 저장됨
	// 	assertThat(result).contains("전세자금대출 정보가 업데이트되었습니다");
	// 	then(restTemplate).should().exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));
	// 	then(houseLoanRepository).should().saveAll(anyList());
	// }

	@Test
	@DisplayName("전세자금대출 정보 업데이트 실패 테스트 - 잘못된 JSON 응답")
	void givenInvalidJsonResponse_whenUpdateRentHouseLoan_thenReturnErrorMessage() {
		// Given: 외부 API에서 잘못된 JSON 형식의 응답을 반환하는 시나리오
		String invalidJsonResponse = "{ invalid json }";

		given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
			.willReturn(ResponseEntity.ok(invalidJsonResponse));

		// When: 전세자금대출 정보 업데이트 메서드 호출
		String result = financeService.updateRentHouseLoan();

		// Then: JSON 파싱 오류 메시지가 반환됨
		assertThat(result).contains("API 데이터 처리 중 오류 발생");
		then(houseLoanRepository).should(never()).saveAll(anyList());
	}

	// @Test
	// @DisplayName("주택담보대출 목록 조회 성공 테스트")
	// void givenMortgageDataExists_whenGetMortgageList_thenReturnMortgageList() {
	// 	// Given: 저장소에 주택담보대출 데이터가 존재하는 시나리오
	// 	List<Mortgage> mortgageList = Arrays.asList(testMortgage);
	// 	given(mortgageRepository.findAll()).willReturn(mortgageList);
	//
	// 	// When: 주택담보대출 목록 조회 메서드 호출
	// 	List<MortgageDTO> result = financeService.getMortgageList();
	//
	// 	// Then: 주택담보대출 DTO 목록이 반환됨
	// 	assertThat(result).hasSize(1);
	// 	assertThat(result.get(0).getKorCoNm()).isEqualTo("테스트은행");
	// 	assertThat(result.get(0).getFinPrdtNm()).isEqualTo("테스트주택담보대출");
	// 	assertThat(result.get(0).getLendRateMin()).isEqualTo(3.5f);
	// 	then(mortgageRepository).should().findAll();
	// }
	//
	// @Test
	// @DisplayName("주택담보대출 목록 조회 테스트 - 데이터 없음")
	// void givenNoMortgageData_whenGetMortgageList_thenReturnEmptyList() {
	// 	// Given: 저장소에 주택담보대출 데이터가 없는 시나리오
	// 	given(mortgageRepository.findAll()).willReturn(Arrays.asList());
	//
	// 	// When: 주택담보대출 목록 조회 메서드 호출
	// 	List<MortgageDTO> result = financeService.getMortgageList();
	//
	// 	// Then: 빈 목록이 반환됨
	// 	assertThat(result).isEmpty();
	// 	then(mortgageRepository).should().findAll();
	// }
	//
	// @Test
	// @DisplayName("전세자금대출 목록 조회 성공 테스트")
	// void givenHouseLoanDataExists_whenGetHouseLoanList_thenReturnHouseLoanList() {
	// 	// Given: 저장소에 전세자금대출 데이터가 존재하는 시나리오
	// 	List<HouseLoan> houseLoanList = Arrays.asList(testHouseLoan);
	// 	given(houseLoanRepository.findAll()).willReturn(houseLoanList);
	//
	// 	// When: 전세자금대출 목록 조회 메서드 호출
	// 	List<HouseLoanDTO> result = financeService.getHouseLoanList();
	//
	// 	// Then: 전세자금대출 DTO 목록이 반환됨
	// 	assertThat(result).hasSize(1);
	// 	assertThat(result.get(0).getKorCoNm()).isEqualTo("테스트저축은행");
	// 	assertThat(result.get(0).getFinPrdtNm()).isEqualTo("테스트전세자금대출");
	// 	assertThat(result.get(0).getLendRateType()).isEqualTo("고정금리");
	// 	then(houseLoanRepository).should().findAll();
	// }
	//
	// @Test
	// @DisplayName("금리별 주택담보대출 필터링 테스트")
	// void givenMortgageDataWithDifferentRates_whenGetMortgageList_thenVerifyRateFiltering() {
	// 	// Given: 다양한 금리의 주택담보대출 데이터가 존재하는 시나리오
	// 	Mortgage lowRateMortgage = Mortgage.builder()
	// 		.finCoNo("0010003")
	// 		.korCoNm("저금리은행")
	// 		.finPrdtNm("저금리상품")
	// 		.lendRateMin(2.0)
	// 		.lendRateMax(3.0)
	// 		.build();
	//
	// 	Mortgage highRateMortgage = Mortgage.builder()
	// 		.finCoNo("0010004")
	// 		.korCoNm("고금리은행")
	// 		.finPrdtNm("고금리상품")
	// 		.lendRateMin(5.0)
	// 		.lendRateMax(7.0)
	// 		.build();
	//
	// 	List<Mortgage> mortgageList = Arrays.asList(lowRateMortgage, testMortgage, highRateMortgage);
	// 	given(mortgageRepository.findAll()).willReturn(mortgageList);
	//
	// 	// When: 주택담보대출 목록 조회 메서드 호출
	// 	List<MortgageDTO> result = financeService.getMortgageList();
	//
	// 	// Then: 모든 상품이 조회되고 금리 정보가 정확히 매핑됨
	// 	assertThat(result).hasSize(3);
	// 	assertThat(result).extracting(MortgageDTO::getLendRateMin)
	// 		.containsExactly(2.0, 3.5, 5.0);
	// 	assertThat(result).extracting(MortgageDTO::getLendRateMax)
	// 		.containsExactly(3.0, 5.5, 7.0);
	// }
}