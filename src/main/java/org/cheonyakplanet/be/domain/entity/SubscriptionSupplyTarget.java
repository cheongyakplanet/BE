package org.cheonyakplanet.be.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.cheonyakplanet.be.domain.Stamped;


import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(catalog = "planet", name = "subscription_supply_target")
public class SubscriptionSupplyTarget extends Stamped {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subscription_info_id")
    private Long subscriptionInfoId;

    @Column(name = "house_manage_no")
    private String houseManageNo;

    @Column(name = "housing_category")
    private String housingCategory;

    @Column(name = "housing_type")
    private String housingType;

    @Column(name = "supply_area", precision = 10, scale = 4)
    private BigDecimal supplyArea;

    @Column(name = "supply_count_normal")
    private Integer supplyCountNormal;

    @Column(name = "supply_count_special")
    private Integer supplyCountSpecial;

    @Column(name = "supply_count_total")
    private Integer supplyCountTotal;

    @Column(name = "house_manage_no_detail")
    private String houseManageNoDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_info_id", insertable = false, updatable = false)
    @JsonBackReference
    private SubscriptionInfo subscriptionInfo;

}
