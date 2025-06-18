package org.cheonyakplanet.be.application.dto.user;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageDTO {
	private String email;
	private String username;
	private List<String> interestLocals;
	private Double property;
	private Integer income;
	private Boolean isMarried;
	private Integer numChild;
	private Integer numHouse;
	private UserStatusEnum status;

	public MyPageDTO(User user) {
		this.email = user.getEmail();
		this.username = user.getUsername();

		this.interestLocals = Stream.of(
				user.getInterestLocal1(),
				user.getInterestLocal2(),
				user.getInterestLocal3(),
				user.getInterestLocal4(),
				user.getInterestLocal5()
			)
			.filter(Objects::nonNull)
			.filter(s -> !s.isBlank())
			.collect(Collectors.toList());

		this.property = user.getProperty();
		this.income = user.getIncome();
		this.isMarried = user.getIsMarried();
		this.numChild = user.getNumChild();
		this.numHouse = user.getNumHouse();
		this.status = user.getStatus();
	}
}
