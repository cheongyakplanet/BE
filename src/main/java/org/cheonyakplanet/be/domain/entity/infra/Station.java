package org.cheonyakplanet.be.domain.entity.infra;

import org.cheonyakplanet.be.domain.Stamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station extends Stamped {
	@Id
	@Column(name = "number", nullable = false)
	private String number; // 고유 식별자로 사용

	@Column(name = "Operator")
	private String operator;

	@Column(name = "line")
	private String line;

	@Column(name = "type")
	private String type;

	@Column(name = "nm_kor")
	private String nmKor;

	@Column(name = "nm_eng")
	private String nmEng;

	@Column(name = "nm_sub")
	private String nmSub;

	@Column(name = "is_transfer")
	private String isTransfer;

	@Column(name = "transfer")
	private String transfer;

	@Column(name = "platform")
	private String platform;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "addr_num")
	private String addrNum;

	@Column(name = "addr_nm")
	private String addrNm;

	@Column(name = "tel")
	private String tel;

	@Column(name = "date_est")
	private Integer dateEst;

	@Column(name = "date_data")
	private Integer dateData;
}
