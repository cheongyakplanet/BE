package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cheonyakplanet.be.domain.Stamped;

@Entity
@Table(catalog = "planet", name = "mortgage_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mortgage extends Stamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK: 자동 생성 ID

    @Column(name = "dcls_month")
    private String dclsMonth; // 공시 월

    @Column(name = "fin_co_no")
    private String finCoNo; // 금융회사 번호

    @Column(name = "fin_prdt_cd")
    private String finPrdtCd; // 금융상품 코드

    @Column(name = "kor_co_nm")
    private String korCoNm; // 금융회사명

    @Column(name = "fin_prdt_nm")
    private String finPrdtNm; // 금융상품명

    @Column(name = "join_way")
    private String joinWay; // 가입 방법

    @Column(name = "loan_inci_expn")
    private String loanInciExpn; // 대출 부대비용

    @Column(name = "erly_rpay_fee")
    private String erlyRpayFee; // 중도상환수수료

    @Column(name = "dly_rate")
    private String dlyRate; // 연체 이자율

    @Column(name = "loan_lmt")
    private String loanLmt; // 대출 한도

    @Column(name = "dcls_strt_day")
    private String dclsStrtDay; // 공시 시작일

    @Column(name = "dcls_end_day")
    private String dclsEndDay; // 공시 종료일

    @Column(name = "fin_co_subm_day")
    private String finCoSubmDay; // 금융회사 제출일

    @Column(name = "mrtg_type")
    private String mrtgType; // 담보 유형 코드

    @Column(name = "mrtg_type_nm")
    private String mrtgTypeNm; // 담보 유형명

    @Column(name = "rpay_type")
    private String rpayType; // 상환 방식 코드

    @Column(name = "rpay_type_nm")
    private String rpayTypeNm; // 상환 방식명

    @Column(name = "lend_rate_type")
    private String lendRateType; // 금리 유형 코드

    @Column(name = "lend_rate_type_nm")
    private String lendRateTypeNm; // 금리 유형명

    @Column(name = "lend_rate_min")
    private Double lendRateMin; // 최저 금리

    @Column(name = "lend_rate_max")
    private Double lendRateMax; // 최고 금리

    @Column(name = "lend_rate_avg")
    private Double lendRateAvg; // 평균 금리
}
