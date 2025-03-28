package org.cheonyakplanet.be.application.dto.user;

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
  private String interestLocal1;
  private String interestLocal2;
  private String interestLocal3;
  private String interestLocal4;
  private String interestLocal5;
  private Double property;
  private Integer income;
  private Boolean isMarried;
  private Integer numChild;
  private Integer numHouse;
  private String status;
}
