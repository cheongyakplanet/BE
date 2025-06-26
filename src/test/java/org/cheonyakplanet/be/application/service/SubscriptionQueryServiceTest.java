package org.cheonyakplanet.be.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.cheonyakplanet.be.application.dto.subscription.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.subscription.SubscriptionDetailDTO;
import org.cheonyakplanet.be.application.dto.subscription.SubscriptionInfoSimpleDTO;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.entity.user.UserStatusEnum;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLocationInfoRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
@DisplayName("청약 조회 서비스 테스트")
class SubscriptionQueryServiceTest {

    @Mock
    private SubscriptionInfoRepository subscriptionInfoRepository;
    
    @Mock
    private SubscriptionLocationInfoRepository subscriptionLocationInfoRepository;
    
    @InjectMocks
    private SubscriptionQueryService subscriptionQueryService;
    
    private SubscriptionInfo testSubscriptionInfo;
    private SubscriptionLocationInfo testLocationInfo;
    private User testUser;
    private UserDetailsImpl userDetails;
    private Long testSubscriptionId;
    
    @BeforeEach
    void setUp() {
        // Given: 테스트용 데이터 설정
        testSubscriptionId = 1L;
        
        testSubscriptionInfo = SubscriptionInfo.builder()
                .id(testSubscriptionId)
                .houseNm("테스트아파트")
                .region("서울특별시")
                .city("강남구")
                .district("역삼동")
                .hssplyAdres("서울특별시 강남구 역삼동 123-45")
                .rceptBgnde(LocalDate.of(2024, 1, 15))
                .rceptEndde(LocalDate.of(2024, 1, 20))
                .spsplyRceptBgnde(LocalDate.of(2024, 1, 10))
                .spsplyRceptEndde(LocalDate.of(2024, 1, 12))
                .przwnerPresnatnDe(LocalDate.of(2024, 1, 25))
                .cntrctCnclsBgnde(LocalDate.of(2024, 2, 1))
                .cntrctCnclsEndde(LocalDate.of(2024, 2, 10))
                .build();
        
        testLocationInfo = SubscriptionLocationInfo.builder()
                .id(testSubscriptionId)
                .latitude("37.5665")
                .longitude("126.9780")
                .build();
        
        testUser = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .role(UserRoleEnum.USER)
                .status(UserStatusEnum.ACTIVE)
                .interestLocal1("서울특별시 강남구")
                .interestLocal2("경기도 성남시")
                .interestLocal3("인천광역시 연수구")
                .interestLocal4("부산광역시 해운대구")
                .interestLocal5("대구광역시 수성구")
                .build();
        
        userDetails = new UserDetailsImpl(testUser);
    }
    
    @Test
    @DisplayName("청약 상세 정보 조회 성공 테스트 - 위치 정보 포함")
    void givenValidSubscriptionId_whenGetSubscriptionById_thenReturnDetailWithLocation() {
        // Given: 유효한 청약 ID와 위치 정보가 존재하는 시나리오
        given(subscriptionInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testSubscriptionInfo));
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        
        // When: 청약 상세 정보 조회 메서드 호출
        SubscriptionDetailDTO result = subscriptionQueryService.getSubscriptionById(testSubscriptionId);
        
        // Then: 위치 정보가 포함된 상세 정보가 반환됨
        assertThat(result).isNotNull();
        assertThat(result.getHouseNm()).isEqualTo("테스트아파트");
        assertThat(result.getRegion()).isEqualTo("서울특별시");
        assertThat(result.getCity()).isEqualTo("강남구");
        
        then(subscriptionInfoRepository).should().findById(testSubscriptionId);
        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
    }
    
    @Test
    @DisplayName("청약 상세 정보 조회 성공 테스트 - 위치 정보 없음")
    void givenValidSubscriptionIdWithoutLocation_whenGetSubscriptionById_thenReturnDetailWithoutLocation() {
        // Given: 유효한 청약 ID지만 위치 정보가 없는 시나리오
        given(subscriptionInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testSubscriptionInfo));
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.empty());
        
        // When: 청약 상세 정보 조회 메서드 호출
        SubscriptionDetailDTO result = subscriptionQueryService.getSubscriptionById(testSubscriptionId);
        
        // Then: 위치 정보 없이 상세 정보가 반환됨
        assertThat(result).isNotNull();
        assertThat(result.getHouseNm()).isEqualTo("테스트아파트");
        assertThat(result.getRegion()).isEqualTo("서울특별시");
        
        then(subscriptionInfoRepository).should().findById(testSubscriptionId);
        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
    }
    
    @Test
    @DisplayName("청약 상세 정보 조회 실패 테스트 - 존재하지 않는 ID")
    void givenInvalidSubscriptionId_whenGetSubscriptionById_thenThrowCustomException() {
        // Given: 존재하지 않는 청약 ID
        given(subscriptionInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.empty());
        
        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            subscriptionQueryService.getSubscriptionById(testSubscriptionId);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO001);
        assertThat(exception.getMessage()).isEqualTo("해당 아이디의 청약건 없음");
        then(subscriptionInfoRepository).should().findById(testSubscriptionId);
    }
    
    @Test
    @DisplayName("지역별 청약 목록 조회 성공 테스트")
    void givenValidRegionAndCity_whenGetSubscriptionsByRegion_thenReturnSubscriptionList() {
        // Given: 유효한 지역과 도시, 그리고 해당 지역의 청약 데이터
        String region = "서울특별시";
        String city = "강남구";
        List<SubscriptionInfo> subscriptions = Arrays.asList(testSubscriptionInfo);
        
        given(subscriptionInfoRepository.findByRegionAndCity(region, city))
                .willReturn(subscriptions);
        
        // When: 지역별 청약 목록 조회 메서드 호출
        List<SubscriptionInfo> result = subscriptionQueryService.getSubscriptionsByRegion(region, city);
        
        // Then: 청약 목록이 반환됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHouseNm()).isEqualTo("테스트아파트");
        assertThat(result.get(0).getRegion()).isEqualTo(region);
        assertThat(result.get(0).getCity()).isEqualTo(city);
        
        then(subscriptionInfoRepository).should().findByRegionAndCity(region, city);
    }
    
    @Test
    @DisplayName("지역별 청약 목록 조회 실패 테스트 - 데이터 없음")
    void givenRegionWithNoSubscriptions_whenGetSubscriptionsByRegion_thenThrowCustomException() {
        // Given: 청약 데이터가 없는 지역
        String region = "제주특별자치도";
        String city = "제주시";
        
        given(subscriptionInfoRepository.findByRegionAndCity(region, city))
                .willReturn(new ArrayList<>());
        
        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            subscriptionQueryService.getSubscriptionsByRegion(region, city);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO002);
        assertThat(exception.getMessage()).isEqualTo("해당 지역의 청약건 없음");
        then(subscriptionInfoRepository).should().findByRegionAndCity(region, city);
    }
    
    @Test
    @DisplayName("청약 리스트 페이징 조회 성공 테스트")
    void givenPageableRequest_whenGetSubscriptions_thenReturnPagedResult() {
        // Given: 페이징 요청과 청약 데이터
        int page = 0;
        int size = 10;
        String sort = "rceptBgnde";
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        List<SubscriptionInfo> subscriptions = Arrays.asList(testSubscriptionInfo);
        Page<SubscriptionInfo> pagedResult = new PageImpl<>(subscriptions, pageable, 1);
        
        given(subscriptionInfoRepository.findAll(pageable))
                .willReturn(pagedResult);
        
        // When: 페이징된 청약 리스트 조회 메서드 호출
        Map<String, Object> result = subscriptionQueryService.getSubscriptions(page, size, sort);
        
        // Then: 페이징된 결과가 반환됨
        assertThat(result).isNotNull();
        assertThat(result.get("totalPages")).isEqualTo(1);
        assertThat(result.get("totalElements")).isEqualTo(1L);
        assertThat(result.get("currentPage")).isEqualTo(1);
        assertThat(result.get("size")).isEqualTo(10);
        
        @SuppressWarnings("unchecked")
        List<SubscriptionDTO> content = (List<SubscriptionDTO>) result.get("content");
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getHouseNm()).isEqualTo("테스트아파트");
        
        then(subscriptionInfoRepository).should().findAll(pageable);
    }
    
    @Test
    @DisplayName("빈 페이지 조회 테스트")
    void givenEmptyPage_whenGetSubscriptions_thenReturnEmptyPagedResult() {
        // Given: 빈 페이지 요청
        int page = 10; // 존재하지 않는 페이지
        int size = 10;
        String sort = "rceptBgnde";
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<SubscriptionInfo> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        
        given(subscriptionInfoRepository.findAll(pageable))
                .willReturn(emptyPage);
        
        // When: 빈 페이지 조회 메서드 호출
        Map<String, Object> result = subscriptionQueryService.getSubscriptions(page, size, sort);
        
        // Then: 빈 결과가 반환됨
        assertThat(result).isNotNull();
        assertThat(result.get("totalPages")).isEqualTo(0);
        assertThat(result.get("totalElements")).isEqualTo(0L);
        
        @SuppressWarnings("unchecked")
        List<SubscriptionDTO> content = (List<SubscriptionDTO>) result.get("content");
        assertThat(content).isEmpty();
        
        then(subscriptionInfoRepository).should().findAll(pageable);
    }
    
    @Test
    @DisplayName("사용자 관심지역 기반 청약 조회 성공 테스트")
    void givenUserWithInterestLocations_whenGetMySubscriptions_thenReturnMatchingSubscriptions() {
        // Given: 관심지역이 설정된 사용자와 해당 지역의 청약 데이터
        List<SubscriptionInfo> kangnamSubscriptions = Arrays.asList(testSubscriptionInfo);
        List<SubscriptionInfo> emptySubscriptions = new ArrayList<>();
        
        given(subscriptionInfoRepository.findByRegionAndCity("서울특별시", "강남구"))
                .willReturn(kangnamSubscriptions);
        given(subscriptionInfoRepository.findByRegionAndCity("경기도", "성남시"))
                .willReturn(emptySubscriptions);
        given(subscriptionInfoRepository.findByRegionAndCity("인천광역시", "연수구"))
                .willReturn(emptySubscriptions);
        given(subscriptionInfoRepository.findByRegionAndCity("부산광역시", "해운대구"))
                .willReturn(emptySubscriptions);
        given(subscriptionInfoRepository.findByRegionAndCity("대구광역시", "수성구"))
                .willReturn(emptySubscriptions);
        
        // When: 사용자 관심지역 기반 청약 조회 메서드 호출
        List<SubscriptionDTO> result = subscriptionQueryService.getMySubscriptions(userDetails);
        
        // Then: 관심지역의 청약만 반환됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHouseNm()).isEqualTo("테스트아파트");
        assertThat(result.get(0).getRegion()).isEqualTo("서울특별시");
        assertThat(result.get(0).getCity()).isEqualTo("강남구");
        
        then(subscriptionInfoRepository).should().findByRegionAndCity("서울특별시", "강남구");
        then(subscriptionInfoRepository).should().findByRegionAndCity("경기도", "성남시");
    }
    
    @Test
    @DisplayName("사용자 관심지역 기반 청약 조회 - 일부 관심지역이 null")
    void givenUserWithPartialInterestLocations_whenGetMySubscriptions_thenHandleNullValues() {
        // Given: 일부 관심지역이 null인 사용자
        User userWithPartialInterests = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .role(UserRoleEnum.USER)
                .status(UserStatusEnum.ACTIVE)
                .interestLocal1("서울특별시 강남구")
                .interestLocal2(null)  // null
                .interestLocal3("")    // 빈 문자열
                .interestLocal4("부산광역시 해운대구")
                .interestLocal5(null)  // null
                .build();
        
        UserDetailsImpl partialUserDetails = new UserDetailsImpl(userWithPartialInterests);
        
        List<SubscriptionInfo> kangnamSubscriptions = Arrays.asList(testSubscriptionInfo);
        List<SubscriptionInfo> busanSubscriptions = new ArrayList<>();
        
        given(subscriptionInfoRepository.findByRegionAndCity("서울특별시", "강남구"))
                .willReturn(kangnamSubscriptions);
        given(subscriptionInfoRepository.findByRegionAndCity("부산광역시", "해운대구"))
                .willReturn(busanSubscriptions);
        
        // When: 관심지역 기반 청약 조회 메서드 호출
        List<SubscriptionDTO> result = subscriptionQueryService.getMySubscriptions(partialUserDetails);
        
        // Then: null이 아닌 관심지역의 청약만 조회됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getHouseNm()).isEqualTo("테스트아파트");
        
        then(subscriptionInfoRepository).should().findByRegionAndCity("서울특별시", "강남구");
        then(subscriptionInfoRepository).should().findByRegionAndCity("부산광역시", "해운대구");
        then(subscriptionInfoRepository).should(never()).findByRegionAndCity(eq(""), any());
    }
    
    @Test
    @DisplayName("사용자 관심지역 기반 청약 조회 - 잘못된 형식의 관심지역")
    void givenUserWithInvalidInterestLocationFormat_whenGetMySubscriptions_thenThrowException() {
        // Given: 잘못된 형식의 관심지역을 가진 사용자
        User userWithInvalidFormat = User.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .role(UserRoleEnum.USER)
                .status(UserStatusEnum.ACTIVE)
                .interestLocal1("잘못된형식")  // 공백으로 구분되지 않음
                .build();
        
        UserDetailsImpl invalidUserDetails = new UserDetailsImpl(userWithInvalidFormat);
        
        // When & Then: IllegalArgumentException 발생 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionQueryService.getMySubscriptions(invalidUserDetails);
        });
        
        assertThat(exception.getMessage()).contains("관심지역 형식이 올바르지 않습니다");
    }
    
    @Test
    @DisplayName("년월별 청약 조회 성공 테스트")
    void givenValidYearMonth_whenGetSubscriptionsByYearMonth_thenReturnMatchingSubscriptions() {
        // Given: 유효한 년월과 해당 기간의 청약 데이터
        int year = 2024;
        int month = 1;
        
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, 31);
        
        List<SubscriptionInfo> subscriptions = Arrays.asList(testSubscriptionInfo);
        
        given(subscriptionInfoRepository.findByRceptBgndeBetweenOrRceptEnddeBetween(
                startOfMonth, endOfMonth, startOfMonth, endOfMonth))
                .willReturn(subscriptions);
        
        // When: 년월별 청약 조회 메서드 호출
        List<SubscriptionInfoSimpleDTO> result = subscriptionQueryService.getSubscriptionsByYearMonth(year, month);
        
        // Then: 해당 기간의 청약이 반환됨
        assertThat(result).hasSize(1);
        
        then(subscriptionInfoRepository).should().findByRceptBgndeBetweenOrRceptEnddeBetween(
                startOfMonth, endOfMonth, startOfMonth, endOfMonth);
    }
    
    @Test
    @DisplayName("년월별 청약 조회 - 데이터 없음")
    void givenYearMonthWithNoSubscriptions_whenGetSubscriptionsByYearMonth_thenReturnEmptyList() {
        // Given: 청약이 없는 년월
        int year = 2025;
        int month = 12;
        
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, 31);
        
        given(subscriptionInfoRepository.findByRceptBgndeBetweenOrRceptEnddeBetween(
                startOfMonth, endOfMonth, startOfMonth, endOfMonth))
                .willReturn(new ArrayList<>());
        
        // When: 년월별 청약 조회 메서드 호출
        List<SubscriptionInfoSimpleDTO> result = subscriptionQueryService.getSubscriptionsByYearMonth(year, month);
        
        // Then: 빈 목록이 반환됨
        assertThat(result).isEmpty();
        
        then(subscriptionInfoRepository).should().findByRceptBgndeBetweenOrRceptEnddeBetween(
                startOfMonth, endOfMonth, startOfMonth, endOfMonth);
    }
    
    @Test
    @DisplayName("2월의 윤년 처리 테스트")
    void givenLeapYearFebruary_whenGetSubscriptionsByYearMonth_thenHandleCorrectly() {
        // Given: 윤년 2월
        int year = 2024; // 윤년
        int month = 2;
        
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, 29); // 윤년이므로 29일까지
        
        given(subscriptionInfoRepository.findByRceptBgndeBetweenOrRceptEnddeBetween(
                startOfMonth, endOfMonth, startOfMonth, endOfMonth))
                .willReturn(new ArrayList<>());
        
        // When: 윤년 2월 청약 조회
        List<SubscriptionInfoSimpleDTO> result = subscriptionQueryService.getSubscriptionsByYearMonth(year, month);
        
        // Then: 정상적으로 처리됨
        assertThat(result).isEmpty();
        
        then(subscriptionInfoRepository).should().findByRceptBgndeBetweenOrRceptEnddeBetween(
                startOfMonth, endOfMonth, startOfMonth, endOfMonth);
    }
}