package org.cheonyakplanet.be.domain.entity;

import org.cheonyakplanet.be.application.dto.user.UserUpdateRequestDTO;
import org.cheonyakplanet.be.domain.Stamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(catalog = "planet", name = "user_info")
@NoArgsConstructor
public class User extends Stamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uuid;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UserRoleEnum role;

	@Column(nullable = false)
	private String username;

	@Column(name = "interest_local_1")
	private String interestLocal1;
	@Column(name = "interest_local_2")
	private String interestLocal2;
	@Column(name = "interest_local_3")
	private String interestLocal3;
	@Column(name = "interest_local_4")
	private String interestLocal4;
	@Column(name = "interest_local_5")
	private String interestLocal5;

	@Column
	private Double property;

	@Column
	private Integer income;

	@Column(name = "is_married")
	private Boolean isMarried;

	@Column(name = "num_child")
	private Integer numChild;

	@Column(name = "num_House")
	private Integer numHouse;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatusEnum status = UserStatusEnum.ACTIVE;

	public User(String email, String password, UserRoleEnum role, String username) {

		this.email = email;
		this.password = password;
		this.role = role;
		this.username = username;
		this.status = UserStatusEnum.ACTIVE;
	}

	public void update(UserUpdateRequestDTO dto) {

		this.username = dto.getUsername() != null ? dto.getUsername() : this.username;
		this.interestLocal1 = dto.getInterestLocal1() != null ? dto.getInterestLocal1() : this.interestLocal1;
		this.interestLocal2 = dto.getInterestLocal2() != null ? dto.getInterestLocal2() : this.interestLocal2;
		this.interestLocal3 = dto.getInterestLocal3() != null ? dto.getInterestLocal3() : this.interestLocal3;
		this.interestLocal4 = dto.getInterestLocal4() != null ? dto.getInterestLocal4() : this.interestLocal4;
		this.interestLocal5 = dto.getInterestLocal5() != null ? dto.getInterestLocal5() : this.interestLocal5;
		this.property = dto.getProperty() != null ? dto.getProperty() : this.property;
		this.income = dto.getIncome() != null ? dto.getIncome() : this.income;
		this.isMarried = dto.getIsMarried() != null ? dto.getIsMarried() : this.isMarried;
		this.numChild = dto.getNumChild() != null ? dto.getNumChild() : this.numChild;
		this.numHouse = dto.getNumHouse() != null ? dto.getNumHouse() : this.numHouse;
		this.status = UserStatusEnum.valueOf("ACTIVE");

		// if (dto.getStatus() != null) {
		//     try {
		//         this.status = UserStatusEnum.valueOf(dto.getStatus().toUpperCase());
		//     } catch (IllegalArgumentException e) {
		//         throw new CustomException(ErrorCode.USER002, "유효하지 않은 상태 값입니다.");
		//     }
		// }
	}

	public void updatePassword(String newPassword) {

		this.password = newPassword;
	}

	public void withdraw(String deletedBy) {

		this.status = UserStatusEnum.WITHDRAWN;
		setDeletedBy(deletedBy);
	}

}
