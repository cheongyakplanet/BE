package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.PublicFacility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicFacilityRepository extends JpaRepository<PublicFacility, Long> {

	List<PublicFacility> findByLatitudeBetweenAndLongitudeBetween(
		double minLatitude, double maxLatitude,
		double minLongitude, double maxLongitude);
}
