package org.cheonyakplanet.be.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class RealEstatePriceSummaryDTO {
	//private Long idx;

	//private String region;

	//private String sggCdNm;

	//private String umdNm;

	private Integer dealYear;

	private Integer dealMonth;

	private Integer dealCount;

	private Long pricePerAr;

	public RealEstatePriceSummaryDTO(String s, String s1, String s2, int i, int i1, int i2, double v) {
		// this.region = s;
		// this.sggCdNm = s1;
		// this.umdNm = s2;
		this.dealYear = i;
		this.dealMonth = i1;
		this.dealCount = i2;
		this.pricePerAr = (long)v;
	}
}
