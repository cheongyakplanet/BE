package org.cheonyakplanet.be.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cheonyakplanet.be.domain.entity.SubscriptionPriceInfo;

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
