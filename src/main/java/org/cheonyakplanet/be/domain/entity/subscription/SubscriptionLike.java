package org.cheonyakplanet.be.domain.entity.subscription;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.cheonyakplanet.be.application.dto.subscriprtion.SubscriptionLikeDTO;
import org.cheonyakplanet.be.domain.Stamped;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "subscription_like")
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionLike extends Stamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public static SubscriptionLikeDTO toDTO(SubscriptionLike entity) {
		return SubscriptionLikeDTO.builder()
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
			.build();
	}

	public void softdelete(String userEmail) {
		this.setDeletedBy(userEmail);
		this.setDeletedAt(LocalDateTime.now());
	}
}
