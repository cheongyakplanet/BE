package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.subscription.SggCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SggCodeRepository extends JpaRepository<SggCode, Integer> {

	@Query("SELECT DISTINCT s.sggCdNmRegion FROM SggCode s")
	List<String> findAllRegions();

	@Query("SELECT DISTINCT s.sggCdNmCity FROM SggCode s where s.sggCdNmRegion= :region")
	List<String> findAllCities(@Param("region") String region);
}