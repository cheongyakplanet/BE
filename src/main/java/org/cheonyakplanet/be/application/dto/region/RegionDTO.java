package org.cheonyakplanet.be.application.dto.region;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegionDTO {
    @Schema(description = "특별시, 도",example = "서울특별시")
    private String region;
}
