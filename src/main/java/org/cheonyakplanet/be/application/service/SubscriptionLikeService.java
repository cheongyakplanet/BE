package org.cheonyakplanet.be.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionLikeDTO;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLike;
import org.cheonyakplanet.be.domain.repository.SubscriptionInfoRepository;
import org.cheonyakplanet.be.domain.repository.SubscriptionLikeRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionLikeService {

	private final SubscriptionInfoRepository subscriptionInfoRepository;
	private final SubscriptionLikeRepository subscriptionLikeRepository;

	/**
	 * 청약 좋아요 추가
	 */
	@Transactional
	public void createSubscriptionLike(UserDetailsImpl userDetails, Long subscriptionId) {
		validateUserLogin(userDetails);

		String userEmail = userDetails.getUsername();

		SubscriptionInfo subscription = subscriptionInfoRepository.findById(subscriptionId)
			.orElseThrow(() -> new CustomException(ErrorCode.INFO001, "청약 정보가 존재하지 않습니다."));

		// 중복 확인
		List<SubscriptionLike> existingLikes = subscriptionLikeRepository.findByCreatedByAndDeletedAtIsNull(userEmail);
		boolean alreadyLiked = existingLikes.stream()
			.anyMatch(like -> like.getSubscriptionId().equals(subscription.getId()) && like.getDeletedAt() == null);

		if (alreadyLiked) {
			throw new CustomException(ErrorCode.INFO007, "이미 좋아요한 청약입니다.");
		}

		SubscriptionLike subscriptionLike = SubscriptionLike.builder()
			.subscriptionId(subscription.getId())
			.houseNm(subscription.getHouseNm())
			.hssplyAdres(subscription.getHssplyAdres())
			.region(subscription.getRegion())
			.city(subscription.getCity())
			.district(subscription.getDistrict())
			.detail(subscription.getDetail())
			.rceptBgnde(subscription.getRceptBgnde())
			.rceptEndde(subscription.getRceptEndde())
			.build();

		subscriptionLikeRepository.save(subscriptionLike);
	}

	/**
	 * 청약 좋아요 삭제
	 */
	@Transactional
	public void deleteSubscriptionLike(UserDetailsImpl userDetails, Long subscriptionId) {
		validateUserLogin(userDetails);

		String userEmail = userDetails.getUsername();

		List<SubscriptionLike> subscriptionLikes = subscriptionLikeRepository.findByCreatedByAndDeletedAtIsNull(
			userEmail);

		SubscriptionLike likeToDelete = subscriptionLikes.stream()
			.filter(like -> Objects.equals(like.getSubscriptionId(), subscriptionId))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.INFO006, "해당 청약에 대한 좋아요 정보가 없습니다."));

		likeToDelete.softdelete(userEmail);
		subscriptionLikeRepository.save(likeToDelete);
	}

	/**
	 * 사용자 좋아요 청약 목록 조회
	 */
	public List<SubscriptionLikeDTO> getLikeSubscription(UserDetailsImpl userDetails) {
		validateUserLogin(userDetails);

		String userEmail = userDetails.getUsername();
		List<SubscriptionLike> subscriptionLikes = subscriptionLikeRepository.findByCreatedByAndDeletedAtIsNull(
			userEmail);

		return subscriptionLikes.stream()
			.map(SubscriptionLikeDTO::fromEntity)
			.collect(Collectors.toList());
	}

	/**
	 * 청약 좋아요 여부 확인
	 */
	public boolean isLikeSubscription(Long subscriptionId, UserDetailsImpl userDetails) {
		SubscriptionLike like = subscriptionLikeRepository.findBySubscriptionIdAndDeletedAtIsNull(subscriptionId);

		if (like == null) {
			return false;
		}

		String userEmail = userDetails.getUsername();
		return userEmail.equals(like.getCreatedBy());
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

	/**
	 * 1주일 이내 청약 종료 목록 조회
	 */
	public List<SubscriptionLikeDTO> getClosingSoonSubscriptionLikes(UserDetailsImpl userDetails) {
		validateUserLogin(userDetails);

		String userEmail = userDetails.getUsername();
		LocalDate today = LocalDate.now();
		LocalDate oneWeekLater = today.plusDays(7);

		return subscriptionLikeRepository.findByCreatedByAndRceptEnddeBetween(userEmail, today, oneWeekLater)
			.stream()
			.map(SubscriptionLikeDTO::fromEntity)
			.collect(Collectors.toList());
	}

	/**
	 * 사용자 로그인 검증
	 */
	private void validateUserLogin(UserDetailsImpl userDetails) {
		if (userDetails == null) {
			throw new CustomException(ErrorCode.SIGN000, "로그인이 필요한 서비스입니다.");
		}
	}
}
