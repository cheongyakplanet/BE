package org.cheonyakplanet.be.application.dto.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO {

	private String number;
	private String name;
	private String line;
	private String operator;
	private boolean isTransfer;
	private Double latitude;
	private Double longitude;
	private Double distance;
}
