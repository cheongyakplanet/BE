package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.finance.HouseLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseLoanRepository extends JpaRepository<HouseLoan, Long> {
}
