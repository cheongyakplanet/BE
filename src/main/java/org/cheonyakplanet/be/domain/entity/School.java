package org.cheonyakplanet.be.domain.entity;

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
@Table(catalog = "planet", name = "school")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School extends Stamped {

	@Id
	@Column(name = "school_ID", nullable = false)
	private String schoolId;

	@Column(name = "school_name", nullable = false)
	private String schoolName;

	@Column(name = "category")
	private String category;

	@Column(name = "type_1")
	private String type1;

	@Column(name = "type_2")
	private String type2;

	@Column(name = "statu")
	private String statu;

	@Column(name = "address")
	private String address;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;
}
