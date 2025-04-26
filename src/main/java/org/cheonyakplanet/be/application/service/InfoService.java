package org.cheonyakplanet.be.application.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.application.dto.infra.InfraResponseDTO;
import org.cheonyakplanet.be.application.dto.infra.PublicFacilityDTO;
import org.cheonyakplanet.be.application.dto.infra.SchoolDTO;
import org.cheonyakplanet.be.application.dto.infra.SchoolListDTO;
import org.cheonyakplanet.be.application.dto.infra.StationDTO;
import org.cheonyakplanet.be.application.dto.infra.StationListDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDetailDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionInfoSimpleDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionLikeDTO;
import org.cheonyakplanet.be.domain.entity.infra.PublicFacility;
import org.cheonyakplanet.be.domain.entity.infra.School;
import org.cheonyakplanet.be.domain.entity.infra.Station;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLike;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.repository.PublicFacilityRepository;
import org.cheonyakplanet.be.domain.repository.SchoolRepository;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.domain.repository.StationRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLikeRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLocationInfoRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfoService {

	private final SubscriptionInfoRepository subscriptionInfoRepository;
	private final SggCodeRepository sggCodeRepository;
	private final SubscriptionLocationInfoRepository subscriptionLocationInfoRepository;
	private final StationRepository stationRepository;
	private final SchoolRepository schoolRepository;
	private final PublicFacilityRepository publicFacilityRepository;
	private final SubscriptionLikeRepository subscriptionLikeRepository;

	// 하버사인 공식을 위한 Earth radius in kilometers
	private static final double EARTH_RADIUS = 6371.0;

	/**
	 * 단일 청약 정보를 조회
	 */
	public Object getSubscriptionById(Long id) {
		// 청약 정보 조회
		Optional<SubscriptionInfo> subscriptionInfoOpt = subscriptionInfoRepository.findById(id);

		if (subscriptionInfoOpt.isEmpty()) {
			throw new CustomException(ErrorCode.INFO001, "해당 아이디의 청약건 없음");
		}

		SubscriptionInfo subscriptionInfo = subscriptionInfoOpt.get();

		// 위치 정보 조회
		Optional<SubscriptionLocationInfo> locationInfoOpt = subscriptionLocationInfoRepository.findById(id);
		SubscriptionLocationInfo locationInfo = locationInfoOpt.orElse(null);

		// DTO 변환 (위치 정보 포함)
		SubscriptionDetailDTO detailDTO = SubscriptionDetailDTO.fromEntity(subscriptionInfo, locationInfo);

		return detailDTO;
	}

	/**
	 * 특정 지역의 청약 목록을 조회
	 */
	public Object getSubscriptionsByRegion(String city, String district) {
		List<SubscriptionInfo> result = subscriptionInfoRepository.findByRegionAndCity(city, district);

		if (result.isEmpty()) {
			return new CustomException(ErrorCode.INFO002, "해당 지역의 청약건 없음");
		} else {
			return result;
		}
	}

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

	/**
	 * 청약 리스트
	 * 마김일 순으로 정렬, 간단 정보만 제공
	 *
	 * @param page
	 * @param size
	 * @return
	 */
	public Object getSubscriptions(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("rceptEndde").descending());

		Page<SubscriptionInfo> result = subscriptionInfoRepository.findAll(pageable);
		List<SubscriptionDTO> subscriptionDTOList = result.stream()
			.map(SubscriptionDTO::fromEntity).collect(Collectors.toList());

		Map<String, Object> response = new HashMap<>();
		response.put("content", subscriptionDTOList);
		response.put("totalPages", result.getTotalPages());
		response.put("totalElements", result.getTotalElements());
		response.put("currentPage", result.getNumber() + 1);
		response.put("size", result.getSize());

		return response;
	}

	public Object getMySubscriptions(UserDetailsImpl userDetails) {
		User user = userDetails.getUser();

		List<String> interests = Arrays.asList(
			user.getInterestLocal1(),
			user.getInterestLocal2(),
			user.getInterestLocal3(),
			user.getInterestLocal4(),
			user.getInterestLocal5()
		);

		// 각 관심지역 문자열을 파싱하여 SubscriptionInfo를 조회한 후 합칩니다.
		List<SubscriptionInfo> subscriptions = interests.stream()
			.filter(Objects::nonNull)
			.filter(s -> !s.isEmpty())
			.flatMap(interestLocal -> {
				String[] parts = interestLocal.split(" ");
				if (parts.length < 2) {
					throw new IllegalArgumentException("관심지역 형식이 올바르지 않습니다: " + interestLocal);
				}
				log.error("지역 : " + parts[0].trim() + "도시 : " + parts[1].trim());
				return subscriptionInfoRepository
					.findByRegionAndCity(parts[0].trim(), parts[1].trim())
					.stream();
			})
			.collect(Collectors.toList());

		return subscriptions.stream()
			.map(SubscriptionDTO::fromEntity)
			.collect(Collectors.toList());
	}

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

		// 주변 2km 내의 역과 학교 조회
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

		// 래퍼 DTO 생성
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

		// 주변 1km 내의 역과 학교 조회
		List<PublicFacility> facilities = findNearbyPublicFacility(latitude, longitude, 1.0);

		// 래퍼 DTO 생성
		List<PublicFacilityDTO> dtoList = facilities.stream()
			.map(facility -> PublicFacilityDTO.builder()
				.dgmNm(facility.getDgmNm())
				.longitude(facility.getLongitude())
				.latitude(facility.getLatitude())
				.build())
			.collect(Collectors.toList());

		return dtoList;
	}

	/**
	 * 위도, 경도를 기준으로 주변 역 조회 (JPA 사용)
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

	private List<PublicFacility> findNearbyPublicFacility(double latitude, double longitude, double radiusInKm) {
		double latDistance = radiusInKm / EARTH_RADIUS * (180.0 / Math.PI);
		double lonDistance = radiusInKm / (EARTH_RADIUS * Math.cos(Math.toRadians(latitude))) * (180.0 / Math.PI);

		double minLat = latitude - latDistance;
		double maxLat = latitude + latDistance;
		double minLon = longitude - lonDistance;
		double maxLon = longitude + lonDistance;

		List<PublicFacility> facilities = publicFacilityRepository.findByLatitudeBetweenAndLongitudeBetween(
			minLat, maxLat, minLon, maxLon);

		// Filter stations by exact distance using Haversine formula
		return facilities.stream()
			.filter(publicFacility ->
				calculateDistance(latitude, longitude, publicFacility.getLatitude(), publicFacility.getLongitude())
					<= radiusInKm)
			.collect(Collectors.toList());
	}

	/**
	 * Haversine formula to calculate distance between two points on Earth
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

	public void createSubscriptionLike(UserDetailsImpl userDetails, Long id) {

		if (userDetails == null) {
			throw new CustomException(ErrorCode.SIGN000, "로그인이 필요한 서비스입니다.");
		}

		Optional<SubscriptionInfo> subscription = subscriptionInfoRepository.findById(id);

		SubscriptionLike subscriptionLike = SubscriptionLike.builder()
			.subscriptionId(id)
			.houseNm(subscription.get().getHouseNm())
			.hssplyAdres(subscription.get().getHssplyAdres())
			.region(subscription.get().getRegion())
			.city(subscription.get().getCity())
			.district(subscription.get().getDistrict())
			.detail(subscription.get().getDetail())
			.rceptBgnde(subscription.get().getRceptBgnde())
			.rceptEndde(subscription.get().getRceptEndde())
			.build();

		subscriptionLikeRepository.save(subscriptionLike);

	}

	public List<SubscriptionLikeDTO> getLikeSubscription(UserDetailsImpl userDetails) {

		if (userDetails == null) {
			throw new CustomException(ErrorCode.SIGN000, "로그인이 필요한 서비스입니다.");
		}

		String userEmail = userDetails.getUsername();
		List<SubscriptionLike> subscriptionLike = subscriptionLikeRepository.findByCreatedBy(userEmail);

		List<SubscriptionLikeDTO> subscriptionLikes = subscriptionLike.stream()
			.map(SubscriptionLikeDTO::fromEntity)
			.collect(Collectors.toList());

		return subscriptionLikes;
	}

	public void deleteSubscriptionLike(UserDetailsImpl userDetails, Long id) {

		if (userDetails == null) {
			throw new CustomException(ErrorCode.SIGN000, "로그인이 필요한 서비스입니다.");
		}

		String userEmail = userDetails.getUsername();

		subscriptionLikeRepository.findById(id).ifPresent(subscriptionLike -> {
			subscriptionLike.softdelete(userEmail);
			subscriptionLikeRepository.save(subscriptionLike);
		});
	}

	public List<SubscriptionLikeDTO> getUpcomingSubscriptionLikes(UserDetailsImpl userDetails) {

		if (userDetails == null) {
			throw new CustomException(ErrorCode.SIGN000, "로그인이 필요한 서비스입니다.");
		}

		String userEmail = userDetails.getUsername();
		LocalDate today = LocalDate.now();
		LocalDate oneWeekLater = today.plusDays(7);

		return subscriptionLikeRepository.findByCreatedByAndRceptBgndeBetween(userEmail, today, oneWeekLater)
			.stream()
			.map(SubscriptionLikeDTO::fromEntity)
			.collect(Collectors.toList());
	}

	public List<SubscriptionLikeDTO> getClosingSoonSubscriptionLikes(UserDetailsImpl userDetails) {

		if (userDetails == null) {
			throw new CustomException(ErrorCode.SIGN000, "로그인이 필요한 서비스입니다.");
		}

		String userEmail = userDetails.getUsername();
		LocalDate today = LocalDate.now();
		LocalDate oneWeekLater = today.plusDays(7);

		return subscriptionLikeRepository.findByCreatedByAndRceptEnddeBetween(userEmail, today, oneWeekLater)
			.stream()
			.map(SubscriptionLikeDTO::fromEntity)
			.collect(Collectors.toList());
	}

	public List<SubscriptionInfoSimpleDTO> getSubscriptionsByYearMonth(int year, int month) {
		LocalDate startOfMonth = LocalDate.of(year, month, 1);
		LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

		List<SubscriptionInfo> result = subscriptionInfoRepository
			.findByRceptBgndeBetweenOrRceptEnddeBetween(
				startOfMonth, endOfMonth, startOfMonth, endOfMonth
			);

		return result.stream()
			.map(SubscriptionInfoSimpleDTO::fromEntity)
			.collect(Collectors.toList());
	}
}
