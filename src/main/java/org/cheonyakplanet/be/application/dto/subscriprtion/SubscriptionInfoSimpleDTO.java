package org.cheonyakplanet.be.application.dto.subscriprtion;

import java.time.LocalDate;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionInfoSimpleDTO {
	private String houseNm;
	private LocalDate rceptBgnde;
	private LocalDate rceptEndde;

	public static SubscriptionInfoSimpleDTO fromEntity(SubscriptionInfo entity) {
		return SubscriptionInfoSimpleDTO.builder()
			.houseNm(entity.getHouseNm())
			.rceptBgnde(entity.getRceptBgnde())
			.rceptEndde(entity.getRceptEndde())
			.build();
	}
}
