package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "real_estate_price_summary")
public class RealEstatePriceSummary {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "region")
	private String region;

	@Column(name = "sgg_cd_nm")
	private String sggCdNm;

	@Column(name = "umd_nm")
	private String umdNm;

	@Column(name = "deal_year")
	private Integer dealYear;

	@Column(name = "deal_month")
	private Integer dealMonth;

	@Column(name = "deal_count")
	private Integer dealCount;

	@Column(name = "price_per_ar")
	private Long pricePerAr;
}
