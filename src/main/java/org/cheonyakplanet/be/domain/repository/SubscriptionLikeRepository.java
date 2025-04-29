package org.cheonyakplanet.be.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionLikeRepository extends JpaRepository<SubscriptionLike, Long> {

	SubscriptionLike findBySubscriptionId(Long subscriptionId);

	List<SubscriptionLike> findByCreatedBy(String createdBy);

	List<SubscriptionLike> findByCreatedByAndRceptBgndeBetween(String createdBy, LocalDate from, LocalDate to);

	List<SubscriptionLike> findByCreatedByAndRceptEnddeBetween(String createdBy, LocalDate from, LocalDate to);
}
