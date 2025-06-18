package org.cheonyakplanet.be.application.dto.infra;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDTO {
	private String schoolId;
	private String schoolName;
	private String category;
	private String type;
	private String address;
	private Double latitude;
	private Double longitude;
	private Double distance;
}
