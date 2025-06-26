package org.cheonyakplanet.be.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.cheonyakplanet.be.application.dto.infra.InfraResponseDTO;
import org.cheonyakplanet.be.application.dto.infra.PublicFacilityDTO;
import org.cheonyakplanet.be.application.dto.infra.SchoolDTO;
import org.cheonyakplanet.be.application.dto.infra.StationDTO;
import org.cheonyakplanet.be.domain.entity.infra.PublicFacility;
import org.cheonyakplanet.be.domain.entity.infra.School;
import org.cheonyakplanet.be.domain.entity.infra.Station;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.cheonyakplanet.be.domain.repository.PublicFacilityRepository;
import org.cheonyakplanet.be.domain.repository.SchoolRepository;
import org.cheonyakplanet.be.domain.repository.StationRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLocationInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("인프라 서비스 테스트")
class InfrastructureServiceTest {

    @Mock
    private SubscriptionLocationInfoRepository subscriptionLocationInfoRepository;
    
    @Mock
    private StationRepository stationRepository;

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private PublicFacilityRepository publicFacilityRepository;

    @InjectMocks
    private InfrastructureService infrastructureService;

    private Station testStation;
    private School testSchool;
    private PublicFacility testPublicFacility;
    private SubscriptionLocationInfo testLocationInfo;
    private Long testSubscriptionId;
    private double testLatitude;
    private double testLongitude;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 데이터 설정
        testSubscriptionId = 1L;
        testLatitude = 37.5665;  // 서울시청 위도
        testLongitude = 126.9780; // 서울시청 경도

        testLocationInfo = SubscriptionLocationInfo.builder()
                .id(testSubscriptionId)
                .latitude("37.5665")
                .longitude("126.9780")
                .build();

        testStation = Station.builder()
                .number("S001")
                .nmKor("테스트역")
                .line("2호선")
                .operator("서울교통공사")
                .isTransfer("N")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        testSchool = School.builder()
                .schoolId("SCH001")
                .schoolName("테스트초등학교")
                .category("초등학교")
                .type1("공립")
                .address("서울특별시 중구 세종대로 110")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        testPublicFacility = PublicFacility.builder()
                .dgmNm("테스트도서관")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();
    }

    @Test
    @DisplayName("청약 물건 주변 인프라 조회 성공 테스트")
    void givenValidSubscriptionId_whenGetNearbyInfrastructure_thenReturnInfraInfo() {
        // Given: 유효한 청약 ID와 주변 인프라 데이터
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        given(stationRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList(testStation));
        given(schoolRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList(testSchool));

        // When: 주변 인프라 조회 메서드 호출
        InfraResponseDTO result = infrastructureService.getNearbyInfrastructure(testSubscriptionId);

        // Then: 인프라 정보가 반환됨
        assertThat(result).isNotNull();
        assertThat(result.getStations()).hasSize(1);
        assertThat(result.getSchools()).hasSize(1);
        
        StationDTO stationDTO = result.getStations().get(0);
        assertThat(stationDTO.getName()).isEqualTo("테스트역");
        assertThat(stationDTO.getLine()).isEqualTo("2호선");
        assertThat(stationDTO.isTransfer()).isFalse();
        
        SchoolDTO schoolDTO = result.getSchools().get(0);
        assertThat(schoolDTO.getSchoolName()).isEqualTo("테스트초등학교");
        assertThat(schoolDTO.getCategory()).isEqualTo("초등학교");
        assertThat(schoolDTO.getType()).isEqualTo("공립");

        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
        then(stationRepository).should().findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        then(schoolRepository).should().findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("청약 물건 주변 인프라 조회 실패 - 위치 정보 없음")
    void givenInvalidSubscriptionId_whenGetNearbyInfrastructure_thenThrowCustomException() {
        // Given: 존재하지 않는 청약 ID
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.empty());

        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            infrastructureService.getNearbyInfrastructure(testSubscriptionId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO003);
        assertThat(exception.getMessage()).isEqualTo("위치 정보가 없는 청약건입니다.");
        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
    }

    @Test
    @DisplayName("청약 물건 주변 인프라 조회 실패 - 위도 정보 null")
    void givenNullLatitude_whenGetNearbyInfrastructure_thenThrowCustomException() {
        // Given: 위도 정보가 null인 위치 정보
        SubscriptionLocationInfo locationWithNullLat = SubscriptionLocationInfo.builder()
                .id(testSubscriptionId)
                .latitude(null)
                .longitude("126.9780")
                .build();
        
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(locationWithNullLat));

        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            infrastructureService.getNearbyInfrastructure(testSubscriptionId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO003);
        assertThat(exception.getMessage()).isEqualTo("위치 정보가 없는 청약건입니다.");
    }

    @Test
    @DisplayName("청약 물건 주변 공공시설 조회 성공 테스트")
    void givenValidSubscriptionId_whenGetNearbyPublicFacility_thenReturnFacilityList() {
        // Given: 유효한 청약 ID와 주변 공공시설 데이터
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        given(publicFacilityRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList(testPublicFacility));

        // When: 주변 공공시설 조회 메서드 호출
        List<PublicFacilityDTO> result = infrastructureService.getNearbyPublicFacility(testSubscriptionId);

        // Then: 공공시설 목록이 반환됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDgmNm()).isEqualTo("테스트도서관");
        assertThat(result.get(0).getLatitude()).isEqualTo(37.5665);
        assertThat(result.get(0).getLongitude()).isEqualTo(126.9780);

        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
        then(publicFacilityRepository).should().findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("청약 물건 주변 공공시설 조회 실패 - 위치 정보 없음")
    void givenInvalidSubscriptionId_whenGetNearbyPublicFacility_thenThrowCustomException() {
        // Given: 존재하지 않는 청약 ID
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.empty());

        // When & Then: CustomException 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> {
            infrastructureService.getNearbyPublicFacility(testSubscriptionId);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INFO003);
        assertThat(exception.getMessage()).isEqualTo("위치 정보가 없는 청약건입니다.");
        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
    }

    @Test
    @DisplayName("주변 인프라 조회 - 데이터 없음")
    void givenNoNearbyInfrastructure_whenGetNearbyInfrastructure_thenReturnEmptyLists() {
        // Given: 주변에 인프라가 없는 시나리오
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        given(stationRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList());
        given(schoolRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList());

        // When: 주변 인프라 조회 메서드 호출
        InfraResponseDTO result = infrastructureService.getNearbyInfrastructure(testSubscriptionId);

        // Then: 빈 목록이 반환됨
        assertThat(result).isNotNull();
        assertThat(result.getStations()).isEmpty();
        assertThat(result.getSchools()).isEmpty();

        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
        then(stationRepository).should().findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble());
        then(schoolRepository).should().findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("환승역 표시 테스트")
    void givenTransferStation_whenGetNearbyInfrastructure_thenMarkAsTransfer() {
        // Given: 환승역 데이터
        Station transferStation = Station.builder()
                .number("S002")
                .nmKor("환승테스트역")
                .line("2호선")
                .operator("서울교통공사")
                .isTransfer("Y")  // 환승역
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        given(stationRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList(transferStation));
        given(schoolRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList());

        // When: 주변 인프라 조회 메서드 호출
        InfraResponseDTO result = infrastructureService.getNearbyInfrastructure(testSubscriptionId);

        // Then: 환승역으로 표시됨
        assertThat(result.getStations()).hasSize(1);
        StationDTO stationDTO = result.getStations().get(0);
        assertThat(stationDTO.getName()).isEqualTo("환승테스트역");
        assertThat(stationDTO.isTransfer()).isTrue();
    }

    @Test
    @DisplayName("거리 계산 및 정렬 테스트")
    void givenMultipleStations_whenGetNearbyInfrastructure_thenSortByDistance() {
        // Given: 거리가 다른 여러 지하철역
        Station nearStation = Station.builder()
                .number("S001")
                .nmKor("가까운역")
                .line("1호선")
                .operator("서울교통공사")
                .isTransfer("N")
                .latitude(37.5665)  // 같은 위치 (거리 0)
                .longitude(126.9780)
                .build();

        Station farStation = Station.builder()
                .number("S002")
                .nmKor("먼역")
                .line("2호선")
                .operator("서울교통공사")
                .isTransfer("N")
                .latitude(37.5700)  // 더 먼 위치
                .longitude(126.9800)
                .build();

        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        given(stationRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList(farStation, nearStation)); // 순서 바뀜
        given(schoolRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList());

        // When: 주변 인프라 조회 메서드 호출
        InfraResponseDTO result = infrastructureService.getNearbyInfrastructure(testSubscriptionId);

        // Then: 거리순으로 정렬되어 반환됨
        assertThat(result.getStations()).hasSize(2);
        assertThat(result.getStations().get(0).getName()).isEqualTo("가까운역");
        assertThat(result.getStations().get(1).getName()).isEqualTo("먼역");
        assertThat(result.getStations().get(0).getDistance()).isLessThan(result.getStations().get(1).getDistance());
    }

    @Test
    @DisplayName("공공시설 조회 - 빈 결과")
    void givenNoNearbyPublicFacilities_whenGetNearbyPublicFacility_thenReturnEmptyList() {
        // Given: 주변에 공공시설이 없는 시나리오
        given(subscriptionLocationInfoRepository.findById(testSubscriptionId))
                .willReturn(Optional.of(testLocationInfo));
        given(publicFacilityRepository.findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .willReturn(Arrays.asList());

        // When: 주변 공공시설 조회 메서드 호출
        List<PublicFacilityDTO> result = infrastructureService.getNearbyPublicFacility(testSubscriptionId);

        // Then: 빈 목록이 반환됨
        assertThat(result).isEmpty();
        then(subscriptionLocationInfoRepository).should().findById(testSubscriptionId);
        then(publicFacilityRepository).should().findByLatitudeBetweenAndLongitudeBetween(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
}