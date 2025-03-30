package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

	List<Station> findByLatitudeBetweenAndLongitudeBetween(
		double minLatitude, double maxLatitude,
		double minLongitude, double maxLongitude);
}
