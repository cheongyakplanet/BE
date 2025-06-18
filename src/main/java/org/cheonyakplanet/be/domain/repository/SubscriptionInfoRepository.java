package org.cheonyakplanet.be.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, Long> {

	// 특정 지역의 청약 리스트 조회
	List<SubscriptionInfo> findByRegionAndCity(String region, String city);

	List<SubscriptionInfo> findByRceptBgndeBetweenOrRceptEnddeBetween(
		LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2
	);

	@Query("SELECT s.houseManageNo FROM SubscriptionInfo s")
	Set<String> findAllHouseManageNos();
}
