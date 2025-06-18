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
public class StationListDTO {
	private List<StationDTO> stationList;
	private int count;
}
