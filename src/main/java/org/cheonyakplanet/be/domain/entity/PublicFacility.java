package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(catalog = "planet", name = "public_facility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicFacility {
	@Id
	@Column(name = "present_sn", nullable = false, length = 30)
	private String presentSn;

	@Column(name = "lclas_cl", length = 10)
	private String lclasCl;

	@Column(name = "mlsfc_cl", length = 10)
	private String mlsfcCl;

	@Column(name = "sclas_cl", length = 10)
	private String sclasCl;

	@Column(name = "atrb_se", length = 5)
	private String atrbSe;

	@Column(name = "wtnnc_sn", length = 30)
	private String wtnncSn;

	@Column(name = "ntfc_sn", length = 30)
	private String ntfcSn;

	@Column(name = "dgm_nm", length = 100)
	private String dgmNm;

	@Column(name = "dgm_ar")
	private Double dgmAr;

	@Column(name = "dgm_lt")
	private Double dgmLt;

	@Column(name = "signgu_se", length = 10)
	private String signguSe;

	@Column(name = "drawing_no", length = 30)
	private String drawingNo;

	@Column(name = "excut_se", length = 10)
	private String excutSe;

	@Column(name = "create_dat", length = 20)
	private String createDat;

	@Column(name = "shape_area")
	private Double shapeArea;

	@Column(name = "shape_len")
	private Double shapeLen;

	@Lob
	@Column(name = "geometry", columnDefinition = "TEXT")
	private String geometry; // Geo 데이터를 문자열로 저장할 경우

	@Column(name = "latlon_geometry", columnDefinition = "TEXT")
	private String latlonGeometry; // 위경도 좌표 리스트를 JSON 형태 등으로 저장

	@Column(name = "mean_latlon", length = 50)
	private String meanLatlon; // 예: "(126.88, 36.63)"

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "latitude")
	private Double latitude;
}
