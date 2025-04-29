package org.cheonyakplanet.be.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

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
@Table(name = "real_estate_price")
public class RealEstatePrice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "region")
	private String region;

	@Column(name = "ssg_cd")
	private String sggCd;

	@Column(name = "ssg_cd_nm")
	private String ssgCdNm;

	@Column(name = "umd_nm")
	private String umdNm;

	@Column(name = "jibun")
	private String jibun;

	@Column(name = "apt_dong")
	private String aptDong;

	@Column(name = "apt_nm")
	private String aptNm;

	@Column(name = "deal_day")
	private Integer dealDay;

	@Column(name = "deal_month")
	private Integer dealMonth;

	@Column(name = "deal_year")
	private Integer dealYear;

	@Column(name = "deal_date")
	private Date dealDate;

	@Column(name = "deal_amount")
	private Long dealAmount;

	@Column(name = "exclu_use_ar")
	private BigDecimal excluUseAr;
}
