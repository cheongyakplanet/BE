package org.cheonyakplanet.be.application.dto.region;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CityDTO {
    @Schema(description = "시,군",example = "서초구")
    private int city;
}
