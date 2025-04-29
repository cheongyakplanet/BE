package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.application.dto.RealEstatePriceSummaryDTO;
import org.cheonyakplanet.be.domain.entity.RealEstatePriceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RealEstatePriceSummaryRepository extends JpaRepository<RealEstatePriceSummary, Long> {
	@Query(
		nativeQuery = true,
		name = "RealEstatePriceSummaryDTOMapping",
		value = "SELECT region, sgg_cd_nm, umd_nm, deal_year, deal_month, deal_count, price_per_ar"
			+ " FROM real_estate_price_summary"
			+ " WHERE region = :region"
			+ "   AND sgg_cd_nm = :sggCdNm"
			+ "   AND umd_nm = :umdNm"
	)
	List<RealEstatePriceSummaryDTO> findByRegionAndSggCdNmAndUmdNm(
		@Param("region") String region,
		@Param("sggCdNm") String sggCdNm,
		@Param("umdNm") String umdNm
	);

	@Modifying
	@Transactional
	@Query(
		nativeQuery = true,
		value = "INSERT INTO real_estate_price_summary"
			+ " (region, sgg_cd_nm, umd_nm, deal_year, deal_month, deal_count, price_per_ar)"
			+ " SELECT region, sgg_cd_nm, umd_nm, deal_year, deal_month, COUNT(*), ROUND(AVG(exclu_use_ar),2)"
			+ " FROM real_estate_price"
			+ " GROUP BY region, sgg_cd_nm, umd_nm, deal_year, deal_month"
	)
	void insertSummary();
}
