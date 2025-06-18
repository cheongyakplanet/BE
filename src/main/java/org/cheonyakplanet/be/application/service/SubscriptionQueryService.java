package org.cheonyakplanet.be.application.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionDetailDTO;
import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionInfoSimpleDTO;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLocationInfo;
import org.cheonyakplanet.be.domain.entity.user.User;
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
public class SubscriptionQueryService {

	private final SubscriptionInfoRepository subscriptionInfoRepository;
	private final SubscriptionLocationInfoRepository subscriptionLocationInfoRepository;

	/**
	 * 단일 청약 정보 상세 조회
	 */
	public SubscriptionDetailDTO getSubscriptionById(Long id) {
		// 청약 정보 조회
		SubscriptionInfo subscriptionInfo = subscriptionInfoRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorCode.INFO001, "해당 아이디의 청약건 없음"));

		// 위치 정보 조회
		Optional<SubscriptionLocationInfo> locationInfoOpt = subscriptionLocationInfoRepository.findById(id);
		SubscriptionLocationInfo locationInfo = locationInfoOpt.orElse(null);

		// DTO 변환 (위치 정보 포함)
		return SubscriptionDetailDTO.fromEntity(subscriptionInfo, locationInfo);
	}

	/**
	 * 특정 지역의 청약 목록 조회
	 */
	public List<SubscriptionInfo> getSubscriptionsByRegion(String region, String city) {
		List<SubscriptionInfo> result = subscriptionInfoRepository.findByRegionAndCity(region, city);

		if (result.isEmpty()) {
			throw new CustomException(ErrorCode.INFO002, "해당 지역의 청약건 없음");
		}

		return result;
	}

	/**
	 * 청약 리스트 조회 (페이징, 정렬)
	 */
	public Map<String, Object> getSubscriptions(int page, int size, String sort) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());

		Page<SubscriptionInfo> result = subscriptionInfoRepository.findAll(pageable);
		List<SubscriptionDTO> subscriptionDTOList = result.stream()
			.map(SubscriptionDTO::fromEntity)
			.collect(Collectors.toList());

		Map<String, Object> response = new HashMap<>();
		response.put("content", subscriptionDTOList);
		response.put("totalPages", result.getTotalPages());
		response.put("totalElements", result.getTotalElements());
		response.put("currentPage", result.getNumber() + 1);
		response.put("size", result.getSize());

		return response;
	}

	/**
	 * 사용자 관심지역 기반 청약 조회
	 */
	public List<SubscriptionDTO> getMySubscriptions(UserDetailsImpl userDetails) {
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
				log.debug("지역 : {} 도시 : {}", parts[0].trim(), parts[1].trim());
				return subscriptionInfoRepository.findByRegionAndCity(parts[0].trim(), parts[1].trim()).stream();
			})
			.collect(Collectors.toList());

		return subscriptions.stream()
			.map(SubscriptionDTO::fromEntity)
			.collect(Collectors.toList());
	}

	/**
	 * 년월별 청약 조회
	 */
	public List<SubscriptionInfoSimpleDTO> getSubscriptionsByYearMonth(int year, int month) {
		java.time.LocalDate startOfMonth = java.time.LocalDate.of(year, month, 1);
		java.time.LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

		List<SubscriptionInfo> result = subscriptionInfoRepository.findByRceptBgndeBetweenOrRceptEnddeBetween(
			startOfMonth, endOfMonth, startOfMonth, endOfMonth);

		return result.stream()
			.map(SubscriptionInfoSimpleDTO::fromEntity)
			.collect(Collectors.toList());
	}
}
