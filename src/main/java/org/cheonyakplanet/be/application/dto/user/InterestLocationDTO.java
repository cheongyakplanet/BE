package org.cheonyakplanet.be.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InterestLocationDTO {
	@Schema(description = "관심 지역", example = "서울시 용산구")
	private String location;
}
