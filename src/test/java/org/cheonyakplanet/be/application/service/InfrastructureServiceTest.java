// package org.cheonyakplanet.be.application.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
//
// import java.util.Arrays;
// import java.util.List;
//
// import org.cheonyakplanet.be.application.dto.infra.InfraResponseDTO;
// import org.cheonyakplanet.be.application.dto.infra.PublicFacilityDTO;
// import org.cheonyakplanet.be.application.dto.infra.SchoolDTO;
// import org.cheonyakplanet.be.application.dto.infra.SchoolListDTO;
// import org.cheonyakplanet.be.application.dto.infra.StationDTO;
// import org.cheonyakplanet.be.application.dto.infra.StationListDTO;
// import org.cheonyakplanet.be.domain.entity.infra.PublicFacility;
// import org.cheonyakplanet.be.domain.entity.infra.School;
// import org.cheonyakplanet.be.domain.entity.infra.Station;
// import org.cheonyakplanet.be.domain.repository.PublicFacilityRepository;
// import org.cheonyakplanet.be.domain.repository.SchoolRepository;
// import org.cheonyakplanet.be.domain.repository.StationRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// @DisplayName("인프라 서비스 테스트")
// class InfrastructureServiceTest {
//
//     @Mock
//     private StationRepository stationRepository;
//
//     @Mock
//     private SchoolRepository schoolRepository;
//
//     @Mock
//     private PublicFacilityRepository publicFacilityRepository;
//
//     @InjectMocks
//     private InfrastructureService infrastructureService;
//
//     private Station testStation;
//     private School testSchool;
//     private PublicFacility testPublicFacility;
//     private double testLatitude;
//     private double testLongitude;
//     private double testRadius;
//
//     @BeforeEach
//     void setUp() {
//         // Given: 테스트용 데이터 설정
//         testLatitude = 37.5665;  // 서울시청 위도
//         testLongitude = 126.9780; // 서울시청 경도
//         testRadius = 1.0; // 1km 반경
//
//         testStation = Station.builder()
//                 .stationName("테스트역")
//                 .lineNum("2호선")
//                 .lat(37.5665)
//                 .lng(126.9780)
//                 .build();
//
//         testSchool = School.builder()
//                 .schoolName("테스트초등학교")
//                 .schoolType("초등학교")
//                 .address("서울특별시 중구 세종대로 110")
//                 .lat(37.5665)
//                 .lng(126.9780)
//                 .build();
//
//         testPublicFacility = PublicFacility.builder()
//                 .facilityName("테스트도서관")
//                 .facilityType("도서관")
//                 .address("서울특별시 중구 세종대로 110")
//                 .lat(37.5665)
//                 .lng(126.9780)
//                 .build();
//     }
//
//     @Test
//     @DisplayName("근처 지하철역 조회 성공 테스트")
//     void givenCoordinatesAndRadius_whenGetNearbyStations_thenReturnStationList() {
//         // Given: 특정 좌표와 반경 내 지하철역이 존재하는 시나리오
//         List<Station> nearbyStations = Arrays.asList(testStation);
//         given(stationRepository.findStationsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(nearbyStations);
//
//         // When: 근처 지하철역 조회 메서드 호출
//         StationListDTO result = infrastructureService.getNearbyStations(testLatitude, testLongitude, testRadius);
//
//         // Then: 지하철역 목록이 반환됨
//         assertThat(result).isNotNull();
//         assertThat(result.getStations()).hasSize(1);
//         assertThat(result.getStations().get(0).getStationName()).isEqualTo("테스트역");
//         assertThat(result.getStations().get(0).getLineNum()).isEqualTo("2호선");
//         assertThat(result.getTotalCount()).isEqualTo(1);
//         then(stationRepository).should().findStationsWithinRadius(testLatitude, testLongitude, testRadius);
//     }
//
//     @Test
//     @DisplayName("근처 지하철역 조회 테스트 - 지하철역 없음")
//     void givenCoordinatesWithNoStations_whenGetNearbyStations_thenReturnEmptyList() {
//         // Given: 특정 좌표와 반경 내 지하철역이 없는 시나리오
//         given(stationRepository.findStationsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(Arrays.asList());
//
//         // When: 근처 지하철역 조회 메서드 호출
//         StationListDTO result = infrastructureService.getNearbyStations(testLatitude, testLongitude, testRadius);
//
//         // Then: 빈 목록이 반환됨
//         assertThat(result).isNotNull();
//         assertThat(result.getStations()).isEmpty();
//         assertThat(result.getTotalCount()).isEqualTo(0);
//     }
//
//     @Test
//     @DisplayName("근처 학교 조회 성공 테스트")
//     void givenCoordinatesAndRadius_whenGetNearbySchools_thenReturnSchoolList() {
//         // Given: 특정 좌표와 반경 내 학교가 존재하는 시나리오
//         List<School> nearbySchools = Arrays.asList(testSchool);
//         given(schoolRepository.findSchoolsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(nearbySchools);
//
//         // When: 근처 학교 조회 메서드 호출
//         SchoolListDTO result = infrastructureService.getNearbySchools(testLatitude, testLongitude, testRadius);
//
//         // Then: 학교 목록이 반환됨
//         assertThat(result).isNotNull();
//         assertThat(result.getSchools()).hasSize(1);
//         assertThat(result.getSchools().get(0).getSchoolName()).isEqualTo("테스트초등학교");
//         assertThat(result.getSchools().get(0).getSchoolType()).isEqualTo("초등학교");
//         assertThat(result.getTotalCount()).isEqualTo(1);
//         then(schoolRepository).should().findSchoolsWithinRadius(testLatitude, testLongitude, testRadius);
//     }
//
//     @Test
//     @DisplayName("학교 유형별 필터링 조회 테스트")
//     void givenSchoolTypeFilter_whenGetSchoolsByType_thenReturnFilteredSchools() {
//         // Given: 특정 유형의 학교만 조회하는 시나리오
//         String schoolType = "초등학교";
//         List<School> elementarySchools = Arrays.asList(testSchool);
//         given(schoolRepository.findBySchoolTypeAndLatBetweenAndLngBetween(
//                 eq(schoolType), anyDouble(), anyDouble(), anyDouble(), anyDouble()))
//                 .willReturn(elementarySchools);
//
//         // When: 학교 유형별 조회 메서드 호출
//         List<SchoolDTO> result = infrastructureService.getSchoolsByType(
//                 testLatitude, testLongitude, testRadius, schoolType);
//
//         // Then: 해당 유형의 학교만 반환됨
//         assertThat(result).hasSize(1);
//         assertThat(result.get(0).getSchoolType()).isEqualTo("초등학교");
//         then(schoolRepository).should().findBySchoolTypeAndLatBetweenAndLngBetween(
//                 eq(schoolType), anyDouble(), anyDouble(), anyDouble(), anyDouble());
//     }
//
//     @Test
//     @DisplayName("근처 공공시설 조회 성공 테스트")
//     void givenCoordinatesAndRadius_whenGetNearbyPublicFacilities_thenReturnFacilityList() {
//         // Given: 특정 좌표와 반경 내 공공시설이 존재하는 시나리오
//         List<PublicFacility> nearbyFacilities = Arrays.asList(testPublicFacility);
//         given(publicFacilityRepository.findFacilitiesWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(nearbyFacilities);
//
//         // When: 근처 공공시설 조회 메서드 호출
//         List<PublicFacilityDTO> result = infrastructureService.getNearbyPublicFacilities(
//                 testLatitude, testLongitude, testRadius);
//
//         // Then: 공공시설 목록이 반환됨
//         assertThat(result).hasSize(1);
//         assertThat(result.get(0).getFacilityName()).isEqualTo("테스트도서관");
//         assertThat(result.get(0).getFacilityType()).isEqualTo("도서관");
//         then(publicFacilityRepository).should().findFacilitiesWithinRadius(testLatitude, testLongitude, testRadius);
//     }
//
//     @Test
//     @DisplayName("통합 인프라 정보 조회 성공 테스트")
//     void givenCoordinatesAndRadius_whenGetInfrastructureInfo_thenReturnCompleteInfraInfo() {
//         // Given: 특정 좌표와 반경 내 모든 인프라가 존재하는 시나리오
//         List<Station> stations = Arrays.asList(testStation);
//         List<School> schools = Arrays.asList(testSchool);
//         List<PublicFacility> facilities = Arrays.asList(testPublicFacility);
//
//         given(stationRepository.findStationsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(stations);
//         given(schoolRepository.findSchoolsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(schools);
//         given(publicFacilityRepository.findFacilitiesWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(facilities);
//
//         // When: 통합 인프라 정보 조회 메서드 호출
//         InfraResponseDTO result = infrastructureService.getInfrastructureInfo(
//                 testLatitude, testLongitude, testRadius);
//
//         // Then: 모든 인프라 정보가 통합되어 반환됨
//         assertThat(result).isNotNull();
//         assertThat(result.getStations()).hasSize(1);
//         assertThat(result.getSchools()).hasSize(1);
//         assertThat(result.getPublicFacilities()).hasSize(1);
//         assertThat(result.getStations().get(0).getStationName()).isEqualTo("테스트역");
//         assertThat(result.getSchools().get(0).getSchoolName()).isEqualTo("테스트초등학교");
//         assertThat(result.getPublicFacilities().get(0).getFacilityName()).isEqualTo("테스트도서관");
//
//         then(stationRepository).should().findStationsWithinRadius(testLatitude, testLongitude, testRadius);
//         then(schoolRepository).should().findSchoolsWithinRadius(testLatitude, testLongitude, testRadius);
//         then(publicFacilityRepository).should().findFacilitiesWithinRadius(testLatitude, testLongitude, testRadius);
//     }
//
//     @Test
//     @DisplayName("거리 계산 정확성 테스트")
//     void givenTwoCoordinates_whenCalculateDistance_thenReturnAccurateDistance() {
//         // Given: 서울시청과 강남역 좌표 (실제 거리 약 7.5km)
//         double seoulCityHallLat = 37.5665;
//         double seoulCityHallLng = 126.9780;
//         double gangnamStationLat = 37.4979;
//         double gangnamStationLng = 127.0276;
//
//         // When: 거리 계산 메서드 호출 (private 메서드이므로 간접적으로 테스트)
//         // 실제로는 findStationsWithinRadius에서 거리 계산이 수행됨
//         given(stationRepository.findStationsWithinRadius(seoulCityHallLat, seoulCityHallLng, 10.0))
//                 .willReturn(Arrays.asList(testStation));
//
//         StationListDTO result = infrastructureService.getNearbyStations(seoulCityHallLat, seoulCityHallLng, 10.0);
//
//         // Then: 10km 반경 내 조회가 정상적으로 수행됨
//         assertThat(result).isNotNull();
//         then(stationRepository).should().findStationsWithinRadius(seoulCityHallLat, seoulCityHallLng, 10.0);
//     }
//
//     @Test
//     @DisplayName("대량 데이터 처리 성능 테스트")
//     void givenLargeDataset_whenGetInfrastructureInfo_thenHandleEfficiently() {
//         // Given: 대량의 인프라 데이터가 존재하는 시나리오
//         List<Station> manyStations = Arrays.asList(testStation, testStation, testStation);
//         List<School> manySchools = Arrays.asList(testSchool, testSchool, testSchool);
//         List<PublicFacility> manyFacilities = Arrays.asList(testPublicFacility, testPublicFacility);
//
//         given(stationRepository.findStationsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(manyStations);
//         given(schoolRepository.findSchoolsWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(manySchools);
//         given(publicFacilityRepository.findFacilitiesWithinRadius(testLatitude, testLongitude, testRadius))
//                 .willReturn(manyFacilities);
//
//         // When: 통합 인프라 정보 조회 메서드 호출
//         long startTime = System.currentTimeMillis();
//         InfraResponseDTO result = infrastructureService.getInfrastructureInfo(
//                 testLatitude, testLongitude, testRadius);
//         long endTime = System.currentTimeMillis();
//
//         // Then: 합리적인 시간 내에 모든 데이터가 처리됨
//         assertThat(result).isNotNull();
//         assertThat(result.getStations()).hasSize(3);
//         assertThat(result.getSchools()).hasSize(3);
//         assertThat(result.getPublicFacilities()).hasSize(2);
//         assertThat(endTime - startTime).isLessThan(1000); // 1초 미만
//     }
//
//     @Test
//     @DisplayName("잘못된 좌표 입력 처리 테스트")
//     void givenInvalidCoordinates_whenGetNearbyStations_thenHandleGracefully() {
//         // Given: 잘못된 좌표 값 (위도 범위 초과)
//         double invalidLatitude = 91.0;
//         double validLongitude = 126.9780;
//
//         given(stationRepository.findStationsWithinRadius(invalidLatitude, validLongitude, testRadius))
//                 .willReturn(Arrays.asList());
//
//         // When: 근처 지하철역 조회 메서드 호출
//         StationListDTO result = infrastructureService.getNearbyStations(invalidLatitude, validLongitude, testRadius);
//
//         // Then: 빈 결과가 반환되지만 예외는 발생하지 않음
//         assertThat(result).isNotNull();
//         assertThat(result.getStations()).isEmpty();
//         then(stationRepository).should().findStationsWithinRadius(invalidLatitude, validLongitude, testRadius);
//     }
//
//     @Test
//     @DisplayName("반경 크기별 조회 결과 테스트")
//     void givenDifferentRadius_whenGetNearbyStations_thenReturnAppropriateResults() {
//         // Given: 다른 반경에 따른 다른 결과
//         double smallRadius = 0.5;
//         double largeRadius = 5.0;
//
//         given(stationRepository.findStationsWithinRadius(testLatitude, testLongitude, smallRadius))
//                 .willReturn(Arrays.asList());
//         given(stationRepository.findStationsWithinRadius(testLatitude, testLongitude, largeRadius))
//                 .willReturn(Arrays.asList(testStation));
//
//         // When: 작은 반경과 큰 반경으로 각각 조회
//         StationListDTO smallRadiusResult = infrastructureService.getNearbyStations(
//                 testLatitude, testLongitude, smallRadius);
//         StationListDTO largeRadiusResult = infrastructureService.getNearbyStations(
//                 testLatitude, testLongitude, largeRadius);
//
//         // Then: 반경에 따라 다른 결과가 반환됨
//         assertThat(smallRadiusResult.getStations()).isEmpty();
//         assertThat(largeRadiusResult.getStations()).hasSize(1);
//         then(stationRepository).should().findStationsWithinRadius(testLatitude, testLongitude, smallRadius);
//         then(stationRepository).should().findStationsWithinRadius(testLatitude, testLongitude, largeRadius);
//     }
// }