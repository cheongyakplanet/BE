package org.cheonyakplanet.be.domain.entity.subscription;

import org.cheonyakplanet.be.domain.Stamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(catalog = "planet", name = "subscription_location_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SubscriptionLocationInfo extends Stamped {

	@Id
	private Long id;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;
}
