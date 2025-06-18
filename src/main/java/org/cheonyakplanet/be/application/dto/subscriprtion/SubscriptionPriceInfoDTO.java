package org.cheonyakplanet.be.application.dto.subscriprtion;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionPriceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPriceInfoDTO {

	private Long id;
	private String houseManageNo;
	private String housingType;
	private Integer supplyPrice;
	private String secondPriorityPayment;
	private String moveInMonth;

	public static SubscriptionPriceInfoDTO fromEntity(SubscriptionPriceInfo entity) {
		if (entity == null) {
			return null;
		}
		return SubscriptionPriceInfoDTO.builder()
			.id(entity.getId())
			.houseManageNo(entity.getHouseManageNo())
			.housingType(entity.getHousingType())
			.supplyPrice(entity.getSupplyPrice())
			.secondPriorityPayment(entity.getSecondPriorityPayment())
			.moveInMonth(entity.getMoveInMonth())
			.build();
	}
}
