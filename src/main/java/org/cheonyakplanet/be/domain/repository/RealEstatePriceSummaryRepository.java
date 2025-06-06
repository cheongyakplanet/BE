package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.RealEstatePriceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RealEstatePriceSummaryRepository extends JpaRepository<RealEstatePriceSummary, Long> {
	@Query(
		nativeQuery = true,
		value = "SELECT region, sgg_cd_nm, umd_nm, deal_year, deal_month, deal_count, price_per_ar"
			+ " FROM real_estate_price_summary"
			+ " WHERE region = :region"
			+ "   AND sgg_cd_nm = :sggCdNm"
			+ "   AND umd_nm = :umdNm"
	)
	List<Object[]> findByRegionAndSggCdNmAndUmdNm(
		@Param("region") String region,
		@Param("sggCdNm") String sggCdNm,
		@Param("umdNm") String umdNm
	);

	@Query(
		nativeQuery = true,
		value =
			"SELECT region, sgg_cd_nm, deal_year, deal_month, sum(deal_count) as deal_count, sum(price_per_ar) as price_per_ar"
				+ " FROM real_estate_price_summary"
				+ " WHERE region = :region"
				+ "   AND sgg_cd_nm = :sggCdNm"
				+ " GROUP BY region, sgg_cd_nm, deal_year, deal_month"
	)
	List<Object[]> findByRegionAndSggCdNm(
		@Param("region") String region,
		@Param("sggCdNm") String sggCdNm
	);

	@Modifying
	@Transactional
	@Query(
		nativeQuery = true,
		value = "INSERT INTO real_estate_price_summary"
			+ " (region, sgg_cd_nm, umd_nm, deal_year, deal_month, deal_count, price_per_ar)"
			+ " SELECT region, sgg_cd_nm, umd_nm, deal_year, deal_month, COUNT(*), AVG(deal_amount / exclu_use_ar) AS price_per_ar"
			+ " FROM real_estate_price"
			+ " GROUP BY region, sgg_cd_nm, umd_nm, deal_year, deal_month"
	)
	void insertSummary();
}
