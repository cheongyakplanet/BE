package org.cheonyakplanet.be.application.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseLoanDTO {
	private String dclsMonth;         // 공시 월
	private String finCoNo;           // 금융회사 번호
	private String finPrdtCd;         // 금융상품 코드
	private String korCoNm;           // 금융회사명
	private String finPrdtNm;         // 금융상품명
	private String joinWay;           // 가입 방법
	private String loanInciExpn;      // 대출 부대비용
	private String erlyRpayFee;       // 중도상환수수료
	private String dlyRate;           // 연체 이자율
	private String loanLmt;           // 대출 한도
	private String dclsStrtDay;       // 공시 시작일
	private String dclsEndDay;        // 공시 종료일
	private String finCoSubmDay;      // 금융회사 제출일
	private String rpayType;          // 상환 방식 코드
	private String rpayTypeNm;        // 상환 방식명
	private String lendRateType;      // 금리 유형 코드
	private String lendRateTypeNm;    // 금리 유형명
	private Double lendRateMin;       // 최저 금리
	private Double lendRateMax;       // 최고 금리
	private Double lendRateAvg;       // 평균 금리
}
