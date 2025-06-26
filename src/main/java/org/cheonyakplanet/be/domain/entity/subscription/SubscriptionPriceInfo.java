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
@Table(catalog = "planet", name = "subscription_price_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SubscriptionPriceInfo extends Stamped {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "subscription_info_id")
	private Long subscriptionInfoId;

	@Column(name = "house_manage_no")
	private String houseManageNo;

	@Column(name = "housing_type")
	private String housingType;

	@Column(name = "supply_price")
	private Integer supplyPrice;

	@Column(name = "second_priority_payment")
	private String secondPriorityPayment;

	@Column(name = "move_in_month")
	private String moveInMonth;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_info_id", insertable = false, updatable = false)
	@JsonBackReference
	private SubscriptionInfo subscriptionInfo;
}
