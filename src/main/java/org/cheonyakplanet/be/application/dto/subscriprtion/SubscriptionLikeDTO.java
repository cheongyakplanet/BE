package org.cheonyakplanet.be.application.dto.subscriprtion;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLike;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionLikeDTO {
	private Long id;

	private Long subscriptionId;

	private String houseNm;                       // 주택명
	private String hssplyAdres;                   // 공급위치
	private String region;                        // 예: 울산광역시
	private String city;                          // 예: 울주군
	private String district;                      // 예: 온양읍
	private String detail;                        // 상세주소

	private LocalDate rceptBgnde; // 청약 접수 시작일
	private LocalDate rceptEndde;

	private String userEmail;

	private LocalDateTime createdAt;

	public static SubscriptionLikeDTO fromEntity(SubscriptionLike entity) {
		SubscriptionLikeDTO dto = SubscriptionLikeDTO.builder()
			.id(entity.getId())
			.subscriptionId(entity.getSubscriptionId())
			.houseNm(entity.getHouseNm())
			.hssplyAdres(entity.getHssplyAdres())
			.region(entity.getRegion())
			.city(entity.getCity())
			.district(entity.getDistrict())
			.detail(entity.getDetail())
			.rceptBgnde(entity.getRceptBgnde())
			.rceptEndde(entity.getRceptEndde())
			.userEmail(entity.getCreatedBy())
			.createdAt(entity.getCreatedAt())
			.build();

		return dto;
	}
}
