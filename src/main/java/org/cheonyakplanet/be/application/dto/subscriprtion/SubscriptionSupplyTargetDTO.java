package org.cheonyakplanet.be.application.dto.subscriprtion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cheonyakplanet.be.domain.entity.SubscriptionSupplyTarget;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionSupplyTargetDTO {
    private Long id;
    private String houseManageNo;
    private String housingCategory;
    private String housingType;
    private BigDecimal supplyArea;
    private Integer supplyCountNormal;
    private Integer supplyCountSpecial;
    private Integer supplyCountTotal;
    private String houseManageNoDetail;

    public static SubscriptionSupplyTargetDTO fromEntity(SubscriptionSupplyTarget entity) {
        if (entity == null) {
            return null;
        }
        return SubscriptionSupplyTargetDTO.builder()
                .id(entity.getId())
                .houseManageNo(entity.getHouseManageNo())
                .housingCategory(entity.getHousingCategory())
                .housingType(entity.getHousingType())
                .supplyArea(entity.getSupplyArea())
                .supplyCountNormal(entity.getSupplyCountNormal())
                .supplyCountSpecial(entity.getSupplyCountSpecial())
                .supplyCountTotal(entity.getSupplyCountTotal())
                .houseManageNoDetail(entity.getHouseManageNoDetail())
                .build();
    }
}
