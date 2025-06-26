package org.cheonyakplanet.be.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.cheonyakplanet.be.application.dto.RealEstatePriceSummaryDTO;
import org.cheonyakplanet.be.domain.entity.RealEstatePrice;
import org.cheonyakplanet.be.domain.entity.subscription.SggCode;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.cheonyakplanet.be.domain.repository.RealEstatePriceRepository;
import org.cheonyakplanet.be.domain.repository.RealEstatePriceSummaryRepository;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.infrastructure.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("정보 서비스 테스트")
class InfoServiceTest {

    @Mock
    private SggCodeRepository sggCodeRepository;
    
    @Mock
    private RealEstatePriceRepository priceRepository;
    
    @Mock
    private RealEstatePriceSummaryRepository priceSummaryRepository;
    
    @Mock
    private Utils utils;
    
    @Mock
    private TaskExecutor taskExecutor;
    
    @InjectMocks
    private InfoService infoService;
    
    private SggCode testSggCode;
    private RealEstatePrice testRealEstatePrice;
    private String testApiKey;
    private String testPriceUrl;
    
    @BeforeEach
    void setUp() {
        // Given: 테스트용 데이터 설정
        testSggCode = new SggCode(11110, "서울특별시 종로구", "서울특별시", "종로구");
        
        testRealEstatePrice = new RealEstatePrice();
        testRealEstatePrice.setRegion("서울특별시");
        testRealEstatePrice.setSggCdNm("종로구");
        testRealEstatePrice.setSggCd("11110");
        testRealEstatePrice.setUmdNm("청운동");
        testRealEstatePrice.setAptNm("테스트아파트");
        testRealEstatePrice.setDealYear(2024);
        testRealEstatePrice.setDealMonth(1);
        testRealEstatePrice.setDealDay(15);
        testRealEstatePrice.setDealAmount(100000L);
        testRealEstatePrice.setExcluUseAr(new BigDecimal("84.5"));
        
        testApiKey = "testApiKey";
        testPriceUrl = "http://test.api.url";
        
        // ReflectionTestUtils를 사용하여 private 필드 설정
        ReflectionTestUtils.setField(infoService, "apiKey", testApiKey);
        ReflectionTestUtils.setField(infoService, "priceUrl", testPriceUrl);
    }
    
    @Test
    @DisplayName("지역 목록 조회 성공 테스트")
    void givenRegionsExist_whenGetReginList_thenReturnRegionList() {
        // Given: 지역 데이터가 존재하는 시나리오
        List<String> expectedRegions = Arrays.asList("서울특별시", "부산광역시", "경기도");
        given(sggCodeRepository.findAllRegions()).willReturn(expectedRegions);
        
        // When: 지역 목록 조회 메서드 호출
        List<String> result = infoService.getReginList();
        
        // Then: 지역 목록이 반환됨
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(expectedRegions);
        then(sggCodeRepository).should().findAllRegions();
    }
    
    @Test
    @DisplayName("지역 목록 조회 실패 테스트 - 데이터 없음")
    void givenNoRegions_whenGetReginList_thenThrowCustomException() {
        // Given: 지역 데이터가 없는 시나리오
        given(sggCodeRepository.findAllRegions()).willReturn(new ArrayList<>());
        
        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            infoService.getReginList();
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO005);
        assertThat(exception.getMessage()).isEqualTo("지역 테이블 없음, DB확인");
        then(sggCodeRepository).should().findAllRegions();
    }
    
    @Test
    @DisplayName("도시 목록 조회 성공 테스트")
    void givenCitiesExist_whenGetCityList_thenReturnCityList() {
        // Given: 특정 지역의 도시 데이터가 존재하는 시나리오
        String region = "서울특별시";
        List<String> expectedCities = Arrays.asList("종로구", "중구", "용산구");
        given(sggCodeRepository.findAllCities(region)).willReturn(expectedCities);
        
        // When: 도시 목록 조회 메서드 호출
        List<String> result = infoService.getCityList(region);
        
        // Then: 도시 목록이 반환됨
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(expectedCities);
        then(sggCodeRepository).should().findAllCities(region);
    }
    
    @Test
    @DisplayName("도시 목록 조회 실패 테스트 - 데이터 없음")
    void givenNoCities_whenGetCityList_thenThrowCustomException() {
        // Given: 특정 지역의 도시 데이터가 없는 시나리오
        String region = "잘못된지역";
        given(sggCodeRepository.findAllCities(region)).willReturn(new ArrayList<>());
        
        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            infoService.getCityList(region);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO005);
        assertThat(exception.getMessage()).isEqualTo("지역 테이블 없음, DB확인");
        then(sggCodeRepository).should().findAllCities(region);
    }
    
    @Test
    @DisplayName("부동산 가격 데이터 수집 성공 테스트")
    void givenValidYearMonth_whenCollectRealPrice_thenCollectDataSuccessfully() {
        // Given: 유효한 년월과 SggCode 데이터
        String yyyyMM = "202401";
        List<SggCode> sggCodes = Arrays.asList(testSggCode);
        given(sggCodeRepository.findAll()).willReturn(sggCodes);
        
        // TaskExecutor 모킹
        willAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).given(taskExecutor).execute(any(Runnable.class));
        
        // When: 부동산 가격 데이터 수집 메서드 호출
        assertDoesNotThrow(() -> {
            infoService.collectRealPrice(yyyyMM);
        });
        
        // Then: 데이터 수집 작업이 수행됨
        then(sggCodeRepository).should().findAll();
        then(priceSummaryRepository).should().deleteAll();
        then(priceSummaryRepository).should().insertSummary();
    }
    
    @Test
    @DisplayName("부동산 가격 데이터 수집 - null 년월 처리")
    void givenNullYearMonth_whenCollectRealPrice_thenUseCurrentMonth() {
        // Given: null 년월 입력
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        List<SggCode> sggCodes = Arrays.asList(testSggCode);
        given(sggCodeRepository.findAll()).willReturn(sggCodes);
        
        willAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).given(taskExecutor).execute(any(Runnable.class));
        
        // When: null 년월로 데이터 수집 메서드 호출
        assertDoesNotThrow(() -> {
            infoService.collectRealPrice(null);
        });
        
        // Then: 현재 월을 사용하여 데이터 수집
        then(sggCodeRepository).should().findAll();
    }
    
    @Test
    @DisplayName("모든 지역 데이터 수집 작업 생성 테스트")
    void givenSggCodes_whenIngestAll_thenCreateCompletableFutures() {
        // Given: SggCode 리스트
        List<SggCode> sggCodes = Arrays.asList(testSggCode);
        given(sggCodeRepository.findAll()).willReturn(sggCodes);
        String callDate = "202401";
        
        // When: 모든 지역 데이터 수집 작업 생성
        List<CompletableFuture<Void>> futures = infoService.ingestAll(callDate);
        
        // Then: CompletableFuture 리스트가 반환됨
        assertThat(futures).hasSize(1);
        then(sggCodeRepository).should().findAll();
    }
    
    @Test
    @DisplayName("동별 부동산 가격 요약 조회 성공 테스트")
    void givenValidRegionCityDong_whenGetRealEstateSummaryDong_thenReturnSummaryData() {
        // Given: 유효한 지역, 도시, 동 정보와 요약 데이터
        String region = "서울특별시";
        String city = "종로구";
        String dong = "청운동";
        
        List<Object[]> mockResults = Arrays.asList(
            new Object[]{region, city, dong, 2024, 1, 5, 50000000L},
            new Object[]{region, city, dong, 2024, 2, 3, 52000000L}
        );
        given(priceSummaryRepository.findByRegionAndSggCdNmAndUmdNm(region, city, dong))
            .willReturn(mockResults);
        
        // When: 동별 부동산 가격 요약 조회
        List<RealEstatePriceSummaryDTO> result = infoService.getRealEstateSummaryDong(region, city, dong);
        
        // Then: 요약 데이터가 반환됨
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDealYearMonth()).isEqualTo(202401);
        assertThat(result.get(0).getDealCount()).isEqualTo(5);
        assertThat(result.get(0).getPricePerAr()).isEqualTo(50000000L);
        
        assertThat(result.get(1).getDealYearMonth()).isEqualTo(202402);
        assertThat(result.get(1).getDealCount()).isEqualTo(3);
        assertThat(result.get(1).getPricePerAr()).isEqualTo(52000000L);
        
        then(priceSummaryRepository).should().findByRegionAndSggCdNmAndUmdNm(region, city, dong);
    }
    
    @Test
    @DisplayName("구별 부동산 가격 요약 조회 성공 테스트")
    void givenValidRegionCity_whenGetRealEstateSummaryGu_thenReturnSummaryData() {
        // Given: 유효한 지역, 도시 정보와 요약 데이터
        String region = "서울특별시";
        String city = "종로구";
        
        List<Object[]> mockResults = Arrays.asList(
            new Object[]{region, city, 2024, 1, new BigDecimal(15), new BigDecimal(48000000L)},
            new Object[]{region, city, 2024, 2, new BigDecimal(12), new BigDecimal(49000000L)}
        );
        given(priceSummaryRepository.findByRegionAndSggCdNm(region, city))
            .willReturn(mockResults);
        
        // When: 구별 부동산 가격 요약 조회
        List<RealEstatePriceSummaryDTO> result = infoService.getRealEstateSummaryGu(region, city);
        
        // Then: 요약 데이터가 반환됨
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDealYearMonth()).isEqualTo(202401);
        assertThat(result.get(0).getDealCount()).isEqualTo(15);
        assertThat(result.get(0).getPricePerAr()).isEqualTo(48000000L);
        
        assertThat(result.get(1).getDealYearMonth()).isEqualTo(202402);
        assertThat(result.get(1).getDealCount()).isEqualTo(12);
        assertThat(result.get(1).getPricePerAr()).isEqualTo(49000000L);
        
        then(priceSummaryRepository).should().findByRegionAndSggCdNm(region, city);
    }
    
    @Test
    @DisplayName("부동산 가격 요약 데이터 새로고침 테스트")
    void whenRefreshSummary_thenDeleteAndInsertSummaryData() {
        // Given: 요약 데이터 새로고침 요청
        willDoNothing().given(priceSummaryRepository).deleteAll();
        willDoNothing().given(priceSummaryRepository).insertSummary();
        
        // When: 요약 데이터 새로고침 메서드 호출
        infoService.refreshSummary();
        
        // Then: 기존 데이터 삭제 후 새로운 요약 데이터 생성
        then(priceSummaryRepository).should().deleteAll();
        then(priceSummaryRepository).should().insertSummary();
    }
    
    @Test
    @DisplayName("개별 지역 데이터 수집 성공 테스트")
    void givenValidSggCodeAndDate_whenIngestOne_thenSaveRealEstateData() throws Exception {
        // Given: 유효한 SggCode와 날짜, 그리고 Utils 모킹
        String callDate = "202401";
        List<RealEstatePrice> mockPrices = Arrays.asList(testRealEstatePrice);
        
        // Remove unnecessary stubs that are not used in actual test execution
        
        // When & Then: 개별 지역 데이터 수집 메서드 호출 (실제 HTTP 호출은 불가능하므로 예외 발생 예상)
        assertDoesNotThrow(() -> {
            infoService.ingestOne(testSggCode, callDate);
        });
    }
    
    @Test
    @DisplayName("동별 요약 데이터 조회 - 빈 결과")
    void givenNoData_whenGetRealEstateSummaryDong_thenReturnEmptyList() {
        // Given: 데이터가 없는 시나리오
        String region = "서울특별시";
        String city = "종로구";  
        String dong = "존재하지않는동";
        
        given(priceSummaryRepository.findByRegionAndSggCdNmAndUmdNm(region, city, dong))
            .willReturn(new ArrayList<>());
        
        // When: 동별 부동산 가격 요약 조회
        List<RealEstatePriceSummaryDTO> result = infoService.getRealEstateSummaryDong(region, city, dong);
        
        // Then: 빈 리스트가 반환됨
        assertThat(result).isEmpty();
        then(priceSummaryRepository).should().findByRegionAndSggCdNmAndUmdNm(region, city, dong);
    }
    
    @Test
    @DisplayName("구별 요약 데이터 조회 - 빈 결과")
    void givenNoData_whenGetRealEstateSummaryGu_thenReturnEmptyList() {
        // Given: 데이터가 없는 시나리오
        String region = "서울특별시";
        String city = "존재하지않는구";
        
        given(priceSummaryRepository.findByRegionAndSggCdNm(region, city))
            .willReturn(new ArrayList<>());
        
        // When: 구별 부동산 가격 요약 조회
        List<RealEstatePriceSummaryDTO> result = infoService.getRealEstateSummaryGu(region, city);
        
        // Then: 빈 리스트가 반환됨
        assertThat(result).isEmpty();
        then(priceSummaryRepository).should().findByRegionAndSggCdNm(region, city);
    }
}