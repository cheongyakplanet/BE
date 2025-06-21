package org.cheonyakplanet.be.application.dto.subscription;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionSpecialSupplyTarget;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionSpecialSupplyTargetDTO {
	private Long id;
	private String houseManageNo;
	private String housingType;
	private Integer supplyCountMultichild;
	private Integer supplyCountNewlywed;
	private Integer supplyCountFirst;
	private Integer supplyCountYouth;
	private Integer supplyCountElderly;
	private Integer supplyCountNewborn;
	private Integer supplyCountInstitutionRecommend;
	private Integer supplyCountPreviousInstitution;
	private Integer supplyCountOthers;
	private Integer supplyCountTotal;

	public static SubscriptionSpecialSupplyTargetDTO fromEntity(SubscriptionSpecialSupplyTarget entity) {
		if (entity == null) {
			return null;
		}
		return SubscriptionSpecialSupplyTargetDTO.builder()
			.id(entity.getId())
			.houseManageNo(entity.getHouseManageNo())
			.housingType(entity.getHousingType())
			.supplyCountMultichild(entity.getSupplyCountMultichild())
			.supplyCountNewlywed(entity.getSupplyCountNewlywed())
			.supplyCountFirst(entity.getSupplyCountFirst())
			.supplyCountYouth(entity.getSupplyCountYouth())
			.supplyCountElderly(entity.getSupplyCountElderly())
			.supplyCountNewborn(entity.getSupplyCountNewborn())
			.supplyCountInstitutionRecommend(entity.getSupplyCountInstitutionRecommend())
			.supplyCountPreviousInstitution(entity.getSupplyCountPreviousInstitution())
			.supplyCountOthers(entity.getSupplyCountOthers())
			.supplyCountTotal(entity.getSupplyCountTotal())
			.build();
	}
}
