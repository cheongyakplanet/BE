package org.cheonyakplanet.be.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CoordinateResponseDTO {
    private String x;
    private String y;
}
