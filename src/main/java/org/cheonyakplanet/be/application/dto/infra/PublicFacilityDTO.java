package org.cheonyakplanet.be.application.dto.infra;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PublicFacilityDTO {
	private String dgmNm;
	private Double longitude;
	private Double latitude;
}
