package org.cheonyakplanet.be.application.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.application.dto.CoordinateResponseDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDetailDTO;
import org.cheonyakplanet.be.domain.entity.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.repository.SggCodeRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
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

	/**
	 * 위도 경도 조회
	 *
	 * @param id
	 * @return
	 */
	public Object getSubscriptionAddr(Long id) {
		Optional<SubscriptionLocationInfo> optionalSubscription = subscriptionLocationInfoRepository.findById(id);
		if (!optionalSubscription.isPresent()) {
			throw new RuntimeException("Subscription not found");
		}
		SubscriptionLocationInfo subscriptionLocationInfo = optionalSubscription.get();
		String lat = subscriptionLocationInfo.getLatitude();
		String lon = subscriptionLocationInfo.getLongitude();
		if (lat != null && lon != null) {
			return new CoordinateResponseDTO(lon, lat);
		}
		throw new RuntimeException("Coordinates not updated yet");
	}

}
