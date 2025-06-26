package org.cheonyakplanet.be.domain.entity.subscription;

import java.math.BigDecimal;

import org.cheonyakplanet.be.domain.Stamped;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
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
