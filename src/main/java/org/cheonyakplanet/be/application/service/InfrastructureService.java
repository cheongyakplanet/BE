package org.cheonyakplanet.be.application.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.application.dto.infra.InfraResponseDTO;
import org.cheonyakplanet.be.application.dto.infra.PublicFacilityDTO;
import org.cheonyakplanet.be.application.dto.infra.SchoolDTO;
import org.cheonyakplanet.be.application.dto.infra.SchoolListDTO;
import org.cheonyakplanet.be.application.dto.infra.StationDTO;
import org.cheonyakplanet.be.application.dto.infra.StationListDTO;
import org.cheonyakplanet.be.domain.entity.infra.PublicFacility;
import org.cheonyakplanet.be.domain.entity.infra.School;
import org.cheonyakplanet.be.domain.entity.infra.Station;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.repository.PublicFacilityRepository;
import org.cheonyakplanet.be.domain.repository.SchoolRepository;
import org.cheonyakplanet.be.domain.repository.StationRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLocationInfoRepository;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인프라 정보 조회 관련 비즈니스 로직을 담당하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InfrastructureService {

	private final SubscriptionLocationInfoRepository subscriptionLocationInfoRepository;
	private final StationRepository stationRepository;
	private final SchoolRepository schoolRepository;
	private final PublicFacilityRepository publicFacilityRepository;

	// 하버사인 공식을 위한 Earth radius in kilometers
	private static final double EARTH_RADIUS = 6371.0;

	/**
	 * 청약 물건 주변 인프라 조회 (역, 학교)
	 */
	public InfraResponseDTO getNearbyInfrastructure(Long subscriptionId) {
		// 청약 물건의 위치 정보 조회
		Optional<SubscriptionLocationInfo> locationOpt = subscriptionLocationInfoRepository.findById(subscriptionId);

		if (locationOpt.isEmpty() || locationOpt.get().getLatitude() == null
			|| locationOpt.get().getLongitude() == null) {
			throw new CustomException(ErrorCode.INFO003, "위치 정보가 없는 청약건입니다.");
		}

		SubscriptionLocationInfo location = locationOpt.get();
		double latitude = Double.parseDouble(location.getLatitude());
		double longitude = Double.parseDouble(location.getLongitude());

		// 주변 1km 내의 역과 학교 조회
		List<Station> nearbyStations = findNearbyStations(latitude, longitude, 1.0);
		List<School> nearbySchools = findNearbySchools(latitude, longitude, 1.0);

		List<StationDTO> stationDTOs = nearbyStations.stream()
			.map(station -> {
				double distance = calculateDistance(latitude, longitude, station.getLatitude(), station.getLongitude());
				return StationDTO.builder()
					.number(station.getNumber())
					.name(station.getNmKor())
					.line(station.getLine())
					.operator(station.getOperator())
					.isTransfer("Y".equals(station.getIsTransfer()))
					.latitude(station.getLatitude())
					.longitude(station.getLongitude())
					.distance(Math.round(distance * 100) / 100.0) // Round to 2 decimal places
					.build();
			})
			.sorted((s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()))
			.collect(Collectors.toList());

		List<SchoolDTO> schoolDTOs = nearbySchools.stream()
			.map(school -> {
				double distance = calculateDistance(latitude, longitude, school.getLatitude(), school.getLongitude());
				return SchoolDTO.builder()
					.schoolId(school.getSchoolId())
					.schoolName(school.getSchoolName())
					.category(school.getCategory())
					.type(school.getType1())
					.address(school.getAddress())
					.latitude(school.getLatitude())
					.longitude(school.getLongitude())
					.distance(Math.round(distance * 100) / 100.0) // Round to 2 decimal places
					.build();
			})
			.sorted((s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()))
			.collect(Collectors.toList());

		StationListDTO stationListDTO = StationListDTO.builder()
			.stationList(stationDTOs)
			.count(stationDTOs.size())
			.build();

		SchoolListDTO schoolListDTO = SchoolListDTO.builder()
			.schoolList(schoolDTOs)
			.count(schoolDTOs.size())
			.build();

		return InfraResponseDTO.builder()
			.stations(stationListDTO.getStationList())
			.schools(schoolListDTO.getSchoolList())
			.build();
	}

	/**
	 * 청약 물건 주변 공공시설 조회
	 */
	public List<PublicFacilityDTO> getNearbyPublicFacility(Long subscriptionId) {
		// 청약 물건의 위치 정보 조회
		Optional<SubscriptionLocationInfo> locationOpt = subscriptionLocationInfoRepository.findById(subscriptionId);

		if (locationOpt.isEmpty() || locationOpt.get().getLatitude() == null
			|| locationOpt.get().getLongitude() == null) {
			throw new CustomException(ErrorCode.INFO003, "위치 정보가 없는 청약건입니다.");
		}

		SubscriptionLocationInfo location = locationOpt.get();
		double latitude = Double.parseDouble(location.getLatitude());
		double longitude = Double.parseDouble(location.getLongitude());

		// 주변 1km 내의 공공시설 조회
		List<PublicFacility> facilities = findNearbyPublicFacility(latitude, longitude, 1.0);

		// DTO 생성
		return facilities.stream()
			.map(facility -> PublicFacilityDTO.builder()
				.dgmNm(facility.getDgmNm())
				.longitude(facility.getLongitude())
				.latitude(facility.getLatitude())
				.build())
			.collect(Collectors.toList());
	}

	/**
	 * 위도, 경도를 기준으로 주변 역 조회
	 */
	private List<Station> findNearbyStations(double latitude, double longitude, double radiusInKm) {
		// Calculate the approximate bounding box for pre-filtering
		double latDistance = radiusInKm / EARTH_RADIUS * (180.0 / Math.PI);
		double lonDistance = radiusInKm / (EARTH_RADIUS * Math.cos(Math.toRadians(latitude))) * (180.0 / Math.PI);

		double minLat = latitude - latDistance;
		double maxLat = latitude + latDistance;
		double minLon = longitude - lonDistance;
		double maxLon = longitude + lonDistance;

		// Get stations within the bounding box
		List<Station> stationsInBox = stationRepository.findByLatitudeBetweenAndLongitudeBetween(
			minLat, maxLat, minLon, maxLon);

		// Filter stations by exact distance using Haversine formula
		return stationsInBox.stream()
			.filter(station -> calculateDistance(latitude, longitude, station.getLatitude(), station.getLongitude())
				<= radiusInKm)
			.collect(Collectors.toList());
	}

	/**
	 * 위도, 경도를 기준으로 주변 학교 조회
	 */
	private List<School> findNearbySchools(double latitude, double longitude, double radiusInKm) {
		// Calculate the approximate bounding box for pre-filtering
		double latDistance = radiusInKm / EARTH_RADIUS * (180.0 / Math.PI);
		double lonDistance = radiusInKm / (EARTH_RADIUS * Math.cos(Math.toRadians(latitude))) * (180.0 / Math.PI);

		double minLat = latitude - latDistance;
		double maxLat = latitude + latDistance;
		double minLon = longitude - lonDistance;
		double maxLon = longitude + lonDistance;

		// Get schools within the bounding box
		List<School> schoolsInBox = schoolRepository.findByLatitudeBetweenAndLongitudeBetween(
			minLat, maxLat, minLon, maxLon);

		// Filter schools by exact distance using Haversine formula
		return schoolsInBox.stream()
			.filter(school -> calculateDistance(latitude, longitude, school.getLatitude(), school.getLongitude())
				<= radiusInKm)
			.collect(Collectors.toList());
	}

	/**
	 * 위도, 경도를 기준으로 주변 공공시설 조회
	 */
	private List<PublicFacility> findNearbyPublicFacility(double latitude, double longitude, double radiusInKm) {
		double latDistance = radiusInKm / EARTH_RADIUS * (180.0 / Math.PI);
		double lonDistance = radiusInKm / (EARTH_RADIUS * Math.cos(Math.toRadians(latitude))) * (180.0 / Math.PI);

		double minLat = latitude - latDistance;
		double maxLat = latitude + latDistance;
		double minLon = longitude - lonDistance;
		double maxLon = longitude + lonDistance;

		List<PublicFacility> facilities = publicFacilityRepository.findByLatitudeBetweenAndLongitudeBetween(
			minLat, maxLat, minLon, maxLon);

		// Filter facilities by exact distance using Haversine formula
		return facilities.stream()
			.filter(publicFacility ->
				calculateDistance(latitude, longitude, publicFacility.getLatitude(), publicFacility.getLongitude())
					<= radiusInKm)
			.collect(Collectors.toList());
	}

	/**
	 * 하버사인 공식으로 거리 계산
	 */
	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return EARTH_RADIUS * c; // Distance in km
	}
}
