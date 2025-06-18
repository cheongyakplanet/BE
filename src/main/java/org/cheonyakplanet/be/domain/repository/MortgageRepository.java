package org.cheonyakplanet.be.domain.repository;

import org.cheonyakplanet.be.domain.entity.finance.Mortgage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageRepository extends JpaRepository<Mortgage, Long> {
}
