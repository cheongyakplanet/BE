package org.cheonyakplanet.be.domain.entity.subscription;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(catalog = "planet", name = "sggcode")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SggCode {

	@Id
	@Column(name = "sgg_cd_5", nullable = false)
	private Integer sggCd5;

	@Column(name = "sgg_cd_nm", columnDefinition = "TEXT")
	private String sggCdNm;

	@Column(name = "sgg_cd_nm_region", columnDefinition = "TEXT")
	private String sggCdNmRegion;

	@Column(name = "sgg_cd_nm_city", columnDefinition = "TEXT")
	private String sggCdNmCity;
}