package org.cheonyakplanet.be.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.cheonyakplanet.be.domain.Stamped;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(catalog = "planet",name = "subscription_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class SubscriptionInfo extends Stamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK: 자동 생성 ID

    @Column(name = "bsns_mby_nm")
    private String bsnsMbyNm; // 사업주체명 (시행사)

    @Column(name = "cnstrct_entrps_nm")
    private String cnstrctEntrpsNm; // 건설업체명 (시공사)

    @Column(name = "cntrct_cncls_bgnde")
    private LocalDate cntrctCnclsBgnde; // 계약시작일

    @Column(name = "cntrct_cncls_endde")
    private LocalDate cntrctCnclsEndde; // 계약종료일

    @Column(name = "gnrl_rnk1_crsparea_endde")
    private LocalDate gnrlRnk1CrspareaEndde; // 1순위 해당지역 접수종료일

    @Column(name = "gnrl_rnk1_crsparea_rcptde")
    private LocalDate gnrlRnk1CrspareaRcptde; // 1순위 해당지역 접수시작일

    @Column(name = "gnrl_rnk1_etc_area_endde")
    private LocalDate gnrlRnk1EtcAreaEndde; // 1순위 기타지역 접수종료일

    @Column(name = "gnrl_rnk1_etc_area_rcptde")
    private LocalDate gnrlRnk1EtcAreaRcptde; // 1순위 기타지역 접수시작일

    @Column(name = "gnrl_rnk1_etc_gg_endde")
    private LocalDate gnrlRnk1EtcGgEndde; // 1순위 경기지역 접수종료일

    @Column(name = "gnrl_rnk1_etc_gg_rcptde")
    private LocalDate gnrlRnk1EtcGgRcptde; // 1순위 경기지역 접수시작일

    @Column(name = "gnrl_rnk2_crsparea_endde")
    private LocalDate gnrlRnk2CrspareaEndde; // 2순위 해당지역 접수종료일

    @Column(name = "gnrl_rnk2_crsparea_rcptde")
    private LocalDate gnrlRnk2CrspareaRcptde; // 2순위 해당지역 접수시작일

    @Column(name = "gnrl_rnk2_etc_area_endde")
    private LocalDate gnrlRnk2EtcAreaEndde; // 2순위 기타지역 접수종료일

    @Column(name = "gnrl_rnk2_etc_area_rcptde")
    private LocalDate gnrlRnk2EtcAreaRcptde; // 2순위 기타지역 접수시작일

    @Column(name = "gnrl_rnk2_etc_gg_endde")
    private LocalDate gnrlRnk2EtcGgEndde; // 2순위 경기지역 접수종료일

    @Column(name = "gnrl_rnk2_etc_gg_rcptde")
    private LocalDate gnrlRnk2EtcGgRcptde; // 2순위 경기지역 접수시작일

    @Column(name = "hmpg_adres")
    private String hmpgAdres; // 홈페이지 주소

    @Column(name = "house_dtl_secd")
    private String houseDtlSecd; // 주택상세구분코드

    @Column(name = "house_dtl_secd_nm")
    private String houseDtlSecdNm; // 주택상세구분코드명

    @Column(name = "house_manage_no")
    private String houseManageNo; // 주택관리번호

    @Column(name = "house_nm")
    private String houseNm; // 주택명

    @Column(name = "house_secd")
    private String houseSecd; // 주택구분코드

    @Column(name = "house_secd_nm")
    private String houseSecdNm; // 주택구분코드명

    @Column(name = "hssply_adres")
    private String hssplyAdres; // 공급위치

    @Column(name = "hssply_zip")
    private String hssplyZip; // 공급위치 우편번호

    @Column(name = "imprmn_bsns_at")
    private String imprmnBsnsAt; // 정비사업 여부

    @Column(name = "lrscl_bldlnd_at")
    private String lrsclBldlndAt; // 대규모 택지개발지구 여부

    @Column(name = "mdat_trget_area_secd")
    private String mdatTrgetAreaSecd; // 조정대상지역 여부

    @Column(name = "mdhs_telno")
    private String mdhsTelno; // 문의처 전화번호

    @Column(name = "mvn_prearnge_ym")
    private String mvnPrearngeYm; // 입주예정월

    @Column(name = "npln_prvopr_public_house_at")
    private String nplnPrvoprPublicHouseAt; // 수도권 내 민영 공공주택지구 여부

    @Column(name = "parcprc_uls_at")
    private String parcprcUlsAt; // 분양가상한제 적용 여부

    @Column(name = "pblanc_no")
    private String pblancNo; // 공고번호

    @Column(name = "pblanc_url")
    private String pblancUrl; // 모집공고 상세 URL

    @Column(name = "przwner_presnatn_de")
    private LocalDate przwnerPresnatnDe; // 당첨자 발표일

    @Column(name = "public_house_earth_at")
    private String publicHouseEarthAt; // 공공주택지구 여부

    @Column(name = "public_house_spclw_applc_at")
    private String publicHouseSpclwApplcAt; // 공공주택 특별법 적용 여부

    @Column(name = "rcept_bgnde")
    private LocalDate rceptBgnde; // 청약 접수 시작일

    @Column(name = "rcept_endde")
    private LocalDate rceptEndde; // 청약 접수 종료일

    @Column(name = "rcrit_pblanc_de")
    private LocalDate rcritPblancDe; // 모집공고일

    @Column(name = "rent_secd")
    private String rentSecd; // 분양구분코드

    @Column(name = "rent_secd_nm")
    private String rentSecdNm; // 분양구분코드명

    @Column(name = "speclt_rdn_earth_at")
    private String specltRdnEarthAt; // 투기과열지구 여부

    @Column(name = "spsply_rcept_bgnde")
    private LocalDate spsplyRceptBgnde; // 특별공급 접수 시작일

    @Column(name = "spsply_rcept_endde")
    private LocalDate spsplyRceptEndde; // 특별공급 접수 종료일

    @Column(name = "subscrpt_area_code")
    private String subscrptAreaCode; // 공급지역코드

    @Column(name = "subscrpt_area_code_nm")
    private String subscrptAreaCodeNm; // 공급지역명

    @Column(name = "tot_suply_hshldco")
    private Integer totSuplyHshldco; // 공급규모

    private String region;
    private String city;
    private String district;
    private String detail;

    @OneToMany(mappedBy = "subscriptionInfo", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SubscriptionPriceInfo> subscriptionPriceInfo;

    @OneToMany(mappedBy = "subscriptionInfo", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SubscriptionSpecialSupplyTarget> subscriptionSpecialSupplyTarget;

    @OneToMany(mappedBy = "subscriptionInfo", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SubscriptionSupplyTarget> subscriptionSupplyTarget;
}
