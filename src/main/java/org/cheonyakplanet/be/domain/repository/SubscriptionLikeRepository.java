package org.cheonyakplanet.be.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.cheonyakplanet.be.domain.entity.subscription.SubscriptionLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionLikeRepository extends JpaRepository<SubscriptionLike, Long> {

	SubscriptionLike findBySubscriptionIdAndDeletedAtIsNull(Long subscriptionId);

	List<SubscriptionLike> findByCreatedByAndDeletedAtIsNull(String createdBy);

	List<SubscriptionLike> findByCreatedByAndRceptBgndeBetween(String createdBy, LocalDate from, LocalDate to);

	List<SubscriptionLike> findByCreatedByAndRceptEnddeBetween(String createdBy, LocalDate from, LocalDate to);

	@Query("SELECT s.subscriptionId FROM SubscriptionLike s " +
		"WHERE s.deletedAt IS NULL " +
		"GROUP BY s.subscriptionId " +
		"ORDER BY COUNT(s) DESC LIMIT 1")
	Long findTopLikedSubscriptionId();
}
