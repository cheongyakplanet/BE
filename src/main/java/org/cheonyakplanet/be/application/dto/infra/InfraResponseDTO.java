package org.cheonyakplanet.be.application.dto.infra;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class InfraResponseDTO {
	private List<StationDTO> stations;
	private List<SchoolDTO> schools;
}
