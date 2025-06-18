package org.cheonyakplanet.be.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDTO {

	private String username;

	@Schema(example = "서울특별시 강남구")
	private String interestLocal1;
	@Schema(example = "서울특별시 종로구")
	private String interestLocal2;
	private String interestLocal3;
	private String interestLocal4;
	private String interestLocal5;
	private Double property;
	private Integer income;
	private Boolean isMarried;
	private Integer numChild;
	private Integer numHouse;
	// @Schema(example = "ACTIVE")
	// private String status;
}
