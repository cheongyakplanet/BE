package org.cheonyakplanet.be.application.dto.subscriprtion;

import java.time.LocalDate;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDTO {
	private Long id;                    // PK
	private String region;              // 예: 울산광역시
	private String city;                // 예: 울주군
	private String district;            // 예: 온양읍
	private String houseManageNo;       // 주택관리번호
	private String houseNm;             // 주택명
	private String bsnsMbyNm;           // 사업주체명(시행사)
	private String houseSecdNm;         // 주택구분코드명 (분양/임대 구분)
	private LocalDate rceptBgnde;       // 청약 접수 시작일
	private LocalDate rceptEndde;       // 청약 접수 종료일
	private Integer totSuplyHshldco;    // 공급규모 (총 공급 가구수)

	public static SubscriptionDTO fromEntity(SubscriptionInfo entity) {
		return SubscriptionDTO.builder()
			.id(entity.getId())
			.region(entity.getRegion())
			.city(entity.getCity())
			.district(entity.getDistrict())
			.houseManageNo(entity.getHouseManageNo())
			.houseNm(entity.getHouseNm())
			.bsnsMbyNm(entity.getBsnsMbyNm())
			.houseSecdNm(entity.getHouseSecdNm())
			.rceptBgnde(entity.getRceptBgnde())
			.rceptEndde(entity.getRceptEndde())
			.totSuplyHshldco(entity.getTotSuplyHshldco())
			.build();
	}
}
