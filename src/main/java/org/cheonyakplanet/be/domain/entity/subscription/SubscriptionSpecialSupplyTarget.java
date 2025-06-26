package org.cheonyakplanet.be.domain.entity.subscription;

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
@Table(catalog = "planet", name = "subscription_special_supply_target")
public class SubscriptionSpecialSupplyTarget extends Stamped {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "subscription_info_id")
	private Long subscriptionInfoId;

	@Column(name = "house_manage_no")
	private String houseManageNo;

	@Column(name = "housing_type")
	private String housingType;

	@Column(name = "supply_count_multichild")
	private Integer supplyCountMultichild;

	@Column(name = "supply_count_newlywed")
	private Integer supplyCountNewlywed;

	@Column(name = "supply_count_first")
	private Integer supplyCountFirst;

	@Column(name = "supply_count_youth")
	private Integer supplyCountYouth;

	@Column(name = "supply_count_elderly")
	private Integer supplyCountElderly;

	@Column(name = "supply_count_newborn")
	private Integer supplyCountNewborn;

	@Column(name = "supply_count_institution_recommend")
	private Integer supplyCountInstitutionRecommend;

	@Column(name = "supply_count_previous_institution")
	private Integer supplyCountPreviousInstitution;

	@Column(name = "supply_count_others")
	private Integer supplyCountOthers;

	@Column(name = "supply_count_total")
	private Integer supplyCountTotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_info_id", insertable = false, updatable = false)
	@JsonBackReference
	private SubscriptionInfo subscriptionInfo;
}
