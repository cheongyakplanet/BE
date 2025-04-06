package org.cheonyakplanet.be.domain.repository;

import java.util.List;

import org.cheonyakplanet.be.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, String> {
	List<School> findByLatitudeBetweenAndLongitudeBetween(
		double minLatitude, double maxLatitude,
		double minLongitude, double maxLongitude);
}
