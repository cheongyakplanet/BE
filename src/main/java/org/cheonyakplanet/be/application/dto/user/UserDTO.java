package org.cheonyakplanet.be.application.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.entity.UserStatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

  private String email;
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
  private UserStatusEnum status;

  public UserDTO(User user) {

    this.email = user.getEmail();
    this.username = user.getUsername();
    this.interestLocal1 = user.getInterestLocal1();
    this.interestLocal2 = user.getInterestLocal2();
    this.interestLocal3 = user.getInterestLocal3();
    this.interestLocal4 = user.getInterestLocal4();
    this.interestLocal5 = user.getInterestLocal5();
    this.property = user.getProperty();
    this.income = user.getIncome();
    this.isMarried = user.getIsMarried();
    this.numChild = user.getNumChild();
    this.numHouse = user.getNumHouse();
    this.status = user.getStatus();
  }
}